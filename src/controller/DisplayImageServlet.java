package controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DisplayImageServlet
 */
@WebServlet("/DisplayImageServlet")
public class DisplayImageServlet extends HttpServlet {




    /*
     * 画像を返すサーブレット
     *
     * パラメータname（JSPから送られる画像ファイル名）を取得して、ファイルを取得して
     * 返している
     *
     * ※日本語のファイルをeclipseで表示しようとするとなぜか表示できない。
     * 　日本語が入ったファイルはFireFoxなどのブラウザで確認してみてください
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //画像があるディレクトリを取得
        String dir = System.getProperty("user.home")+"/output_imgfile";
        //imgタグで指定されたファイル名を取得する 「?name=」の部分
        String fname = request.getParameter("name");

        int iData = 0;

        //ServletのOutputStream取得
        ServletOutputStream out = null;
        BufferedInputStream in = null;

        try {
            out = response.getOutputStream();

            //画像ファイルをBufferedInputStreamを使用して読み取る
            in = new BufferedInputStream(new FileInputStream(dir+"/"+fname));

            //画像を書き出す
            while((iData = in.read()) != -1){
                out.write(iData);
            }
        }catch(Exception e) {
            //ログを吐くなどのエラー処理、デフォルト画像を返すなど

        }finally {
            //クローズ
            if( in != null )    in.close();
            if( out != null )    out.close();
        }
    }


    /*
     * 【追加情報】（将来の為に知っておくと良いこと。「ほんとの仕事の時はこうなるよ」って話）
     *
     * ・アップロードされた画像を表示する方法は色々ある。今回のこのサーブレットを介した方法が唯一解ではない
     *
     * ・画像をアップロードするディレクトリが「公開ディレクト」であれば、このようにわざわざサーブレットを
     * 　解する必要なく、ファイル名だけでもいけるはず。
     * 　→実際、昔の業務ではそうしていた気がする（うろ覚え）
     *
     * ・今回、このようなややこしい方法をとったのは、「公開フォルダ」の理解が浅いだろうという予想と
     * 　本番サーバーに持って行ってもそのまま動くもの且つセキュリティ的にもゆるすぎないものという意図で
     * 　こういう形になった
     */

}
