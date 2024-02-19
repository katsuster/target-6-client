package net.katsuster.draw;

import java.awt.*;

/**
 * 描画可能なオブジェクトを示すインターフェースです。
 * Drawableを実装したオブジェクトをScenarioのaddDrawable()でシナリオに登録し、drawAll()メソッドを呼び出すことで描画されます。
 */
public interface Drawable {
    enum H_ALIGN {
        /**
         * X座標をコンテンツ領域の左端に配置します。
         */
        LEFT,

        /**
         * X座標をコンテンツ領域の中央に配置します。
         */
        CENTER,

        /**
         * X座標をコンテンツ領域の右端に配置します。
         */
        RIGHT,
    }

    enum V_ALIGN {
        /**
         * Y座標をコンテンツ領域の上部に配置します。
         */
        TOP,

        /**
         * Y座標をコンテンツ領域の中央に配置します。
         */
        CENTER,

        /**
         * Y座標をコンテンツ領域の下部に配置します。
         */
        BOTTOM,
    }

    enum SCALE {
        /**
         * コンテンツ領域の大きさに合うように縮小または拡大します。
         */
        JUST,

        /**
         * コンテンツ領域の大きさに合うように縮小または拡大しますが、縦横比は維持します。
         */
        JUST_AND_KEEP_ASPECT,

        /**
         * コンテンツ領域の大きさに合うように縮小します。拡大はしません。
         */
        SHRINK,

        /**
         * コンテンツ領域の大きさに合うように縮小しますが、縦横比は維持します。
         */
        SHRINK_AND_KEEP_ASPECT,
    }

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
     * 図形内部を塗りつぶすかどうかを取得します。
     *
     * @return 図形内部を塗りつぶすならばtrue、そうでなければfalse
     */
    boolean getFilled();

    /**
     * 図形内部を塗りつぶすかどうかを設定します。
     *
     * @param f 図形内部を塗りつぶすならばtrue、そうでなければfalse
     */
    void setFilled(boolean f);

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
     * 枠を描画するための図形を取得します。
     *
     * @return 枠を描画するための図形
     */
    Stroke getStroke();

    /**
     * 枠を描画するための図形を設定します。
     *
     * @param s 枠を描画するための図形
     */
    void setStroke(Stroke s);

    /**
     * 水平方向の位置を取得します。
     *
     * @return 水平方向の位置
     */
    H_ALIGN getHAlign();

    /**
     * 水平方向の位置を設定します。
     *
     * @param h 水平方向の位置
     */
    void setHAlign(H_ALIGN h);

    /**
     * 垂直方向の位置を取得します。
     *
     * @return 垂直方向の位置
     */
    V_ALIGN getVAlign();

    /**
     * 垂直方向の位置を設定します。
     *
     * @param v 垂直方向の位置
     */
    void setVAlign(V_ALIGN v);

    /**
     * 水平方向と垂直方向の位置を同時に設定します。
     *
     * @param h 水平方向の位置
     * @param v 垂直方向の位置
     */
    void setAlign(H_ALIGN h, V_ALIGN v);

    /**
     * 拡大縮小のポリシーを取得します。
     *
     * @return 拡大縮小のポリシー
     */
    SCALE getScale();

    /**
     * 拡大縮小のポリシーを設定します。
     *
     * @param s 拡大縮小のポリシー
     */
    void setScale(SCALE s);

    /**
     * このオブジェクトを描画します。
     *
     * @param g2 描画に使用するグラフィクスコンテキスト
     */
    void draw(Graphics2D g2);
}
