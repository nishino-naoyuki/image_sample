package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig(location="")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////
	//★ポイント★
	// １．JSP側のformタグでenctype="multipart/form-data"を指定する。
	//　　これを指定することで、メールで言うところ「添付ファイル」を送るようなイメージ
	//　　ようするにバイナリデータをサーバーに送ることができる。
	//    詳しく知りたい人は↓
	//　　https://www.yoheim.net/blog.php?q=20171201
	//
	//２．@MultipartConfigをサーブレットにつける
	//　　このアノテーションについて詳しく知りたい人は↓
	//　　https://qiita.com/ohke/items/bec00a69d3f538aab06b
	//　　http://itdoc.hitachi.co.jp/manuals/link/cosmi_v0970/03Y2160D/EY210141.HTM#ID00601
	//
	//３．multipart/form-dataで送られた情報は、getParamterではなく、getPartで取得する
	//
	//４．リクエストヘッダ内のContent-Dispositionに送られたファイルのファイル名がある
	//
	//５．送られたファイルをwriteで書きだす。
	//　　出力場所はどこでも良いが、環境（ローカルor本番サーバー）で異なるはずなので注意する
	//
	//６．アップロードファイルをDBに保存する場合は、ファイルの名前を保存すればよい（出力フォルダが固定だから）
	//
	//※System.getProperty("user.home")　はWindowsとmacのパスの差を吸収するために使用
	//  Windowsの場合は「C:\Users\nishino」が返る（わからんかったらググろう）
	///////////////////////////////////////////////
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		///////////////////////////////////////////////////////
		//画像と一緒に送られてきた文字列のパラメータを取得する
		///////////////////////////////////////////////////////
		String title = getStringParamFromPart(request);

		/////////////////////////////////////////////////////
		//画像に関する処理
		///////////////////////////////////////////////////
		String outputDir =System.getProperty("user.home")+"/output_imgfile";	//←このパスはそれぞれの環境に合わせて修正が必要

		//送られてきたファイルの情報を取得
		Part part = request.getPart("imgFile");	//※"imgFile" は upload.jspにあるinputタグの name="imgFile"より
		//アップロードされたファイル名を取得する
        String filename = this.getFileName(part);
        //出力フォルダが無ければ作る
        makeDir(outputDir);
        //outputDirで指定した場所へコピーする
        part.write(outputDir + "/" + filename);

		/////////////////////////////////////////////////////
		//画面遷移に関する処理
		///////////////////////////////////////////////////
        //リクエストスコープに値をセット
        request.setAttribute("title", title);
        request.setAttribute("filename", filename);
        //画面遷移
        RequestDispatcher dispacher = request.getRequestDispatcher("WEB-INF/jsp/confirm.jsp");
		dispacher.forward(request, response);
    }



	/**
	 * アップロードされたファイル名をヘッダ情報より取得する
	 * @param part
	 * @return
	 */
    private String getFileName(Part part) {
        String name = null;
        for (String dispotion : part.getHeader("Content-Disposition").split(";")) {
            if (dispotion.trim().startsWith("filename")) {
                name = dispotion.substring(dispotion.indexOf("=") + 1).replace("\"", "").trim();
                name = name.substring(name.lastIndexOf("\\") + 1);
                break;
            }
        }
        return name;
    }



	/**
	 * Multipartから文字列パラメータを取得する
	 * @param request
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws ServletException
	 */
	private String getStringParamFromPart(HttpServletRequest request) throws  IllegalStateException, IOException, ServletException{
		String sparam = null;
		Part part;

		part = request.getPart("title");	//この「title」はJSPで指定されたname属性 <input type="text" name="title"・・・
		String contentType = part.getContentType();

        if ( contentType == null) {
            try(InputStream inputStream = part.getInputStream()) {
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
                sparam = bufReader.lines().collect(Collectors.joining(System.getProperty("line.separator")));

            } catch (IOException e) {
            	throw e;
            }
        }

        return sparam;

	}

	/**
	 * ディレクトリ作成
	 * @param dir
	 */
	public static void makeDir(String dir){
		//Fileオブジェクトを生成する
        File f = new File(dir);

        if (!f.exists()) {
            //フォルダ作成実行
            f.mkdirs();
        }
	}

	/*
	 * 【追加情報】（将来の為に知っておくと良いこと。「ほんとの仕事の時はこうなるよ」って話）
	 *
	 * ・本来、画像ファイルの出力先は設定ファイルなどに持ち、切り替えらえるようにしておくと良い。
	 *
	 * ・ファイルの出力先はWebContent以外の場所、できればワークスペースとは関係ないところが良い
	 * 　なぜならば、WebContentの中に設定すると、サーバーでデプロイされたディレクトリの中に
	 * 　ファイルが出力されてしまう。
	 * 　そうなると、たとえばアプリに修正があってwarファイルを差し替えデプロイしなおすときに
	 * 　人によっては現在展開されているアプリのディレクトリを消してしまうかもしれないから。
	 *
	 * ・コーディングの話。今回、ソースコードの単純化のために出力のディレクトリは、ハードコーディング
	 * 　しているが、本当は定数とすべき。そうしないと修正時に複数個所変更することになる
	 *
	 * ・【結構重要】ファイル名は今回は、アップロードされたものをそのまま使用しているが、本当はこれはNG。
	 * 　なぜならば、ほかのだれかが同じファイル名の別ファイルをアップすると上書きされるため。
	 * 　通常は、ファイル名がかぶらないようにサーバーに保存する際にファイル名のお知りにミリ秒までついた
	 * 　アップロード日時をつけるか、GUIなどのかぶらない値を取得してファイル名とする
	 */

}
