package net.katsuster.draw;

import java.awt.*;

/**
 * 描画可能なオブジェクトを示すインターフェースです。
 * Drawableを実装したオブジェクトをScenarioのaddDrawable()でシナリオに登録し、drawAll()メソッドを呼び出すことで描画されます。
 */
public interface Drawable {
    /**
     * コンテンツの描画領域を取得します。
     *
     * @return コンテンツの描画領域
     */
    ContentBox getContentBox();

    /**
     * 表示可能かどうかを取得します。
     *
     * @return 表示可能ならばtrue、そうでなければfalse
     */
    boolean getVisible();

    /**
     * 表示可能かどうかを設定します。
     *
     * @param v 表示可能ならばtrue、そうでなければfalse
     */
    void setVisible(boolean v);

    /**
     * 前面の色を取得します。
     *
     * @return 前面の色
     */
    Color getForeground();

    /**
     * 前面の色を設定します。
     *
     * @param c 前面の色
     */
    void setForeground(Color c);

    /**
     * 背景の色を設定します。
     *
     * @return 背景の色
     */
    Color getBackground();

    /**
     * 背景の色を設定します。
     *
     * @param c 背景の色
     */
    void setBackground(Color c);

    /**
     * テキスト描画に用いるフォントを取得します。
     *
     * @return フォント
     */
    Font getFont();

    /**
     * テキスト描画に用いるフォントを設定します。
     *
     * @param f フォント
     */
    void setFont(Font f);

    /**
     * このオブジェクトを描画します。
     *
     * @param g2 描画に使用するグラフィクスコンテキスト
     */
    void draw(Graphics2D g2);
}
