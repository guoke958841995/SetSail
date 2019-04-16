package com.sxhalo.PullCoal.ui.guide;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * 引导指南
 *  使用 ：if(GuidePageManager.hasNotShowed(this, MainActivity.class.getSimpleName())){  // 用与判断当前引导是否已经显示
 *             new GuidePage.Builder(this)
 *                         .setLayoutId(R.layout.view_home_guide_page)  //要展示的引导界面布局
 *                         .setKnowViewId(R.id.btn_home_act_enter_know) //点击按钮关闭的空件Id
 *                         .setPageTag(MainActivity.class.getSimpleName())  //其中 这里的MainActivity.class.getSimpleName() 要和上面的 MainActivity.class.getSimpleName() 一至
 *                         .builder()
 *                         .apply();
 *         }
 */
public class GuidePage {
    private int layoutId;
    private int knowViewId;  //点击取消
    private int nextViewId;  //点击下一步
    private String pageTag;
    private boolean mCancel = false;

    private Activity activity;
    private FrameLayout rootLayout;
    private View layoutView;

    private int pos = 0; //记录下一步点击了几次

    //设置为 protected或private, 不被外部直接调用
    protected GuidePage(){
    }

    public static class Builder{
        private GuidePage guidePage = new GuidePage();


        public Builder(Activity activity){
            guidePage.activity = activity;
        }

        public Builder setLayoutId(@LayoutRes int layoutId){
            guidePage.layoutId = layoutId;
            return this;
        }

        /**
         * 设置点击消失空间
         * @param knowViewId
         * @return
         */
        public Builder setKnowViewId(@IdRes int knowViewId){
            guidePage.knowViewId = knowViewId;
            return this;
        }

        /**
         * 设置 点击下一步操作
         * @param nextViewId
         * @param nextBack
         * @return
         */
        public Builder setNextViewId(@IdRes int nextViewId,NextBack nextBack) {
            guidePage.nextViewId = nextViewId;
            guidePage.nextBack = nextBack;
            return this;
        }


        /**
         * 引导唯一的标记，用作存储到SharedPreferences的key值，不同引导页必须不一样
         * @param pageTag
         * @return
         */
        public Builder setPageTag(String pageTag){
            guidePage.pageTag = pageTag;
            return this;
        }

        public Builder setCloseOnTouchOutside(boolean cancel){
            guidePage.mCancel = cancel;
            return this;
        }

        public GuidePage builder(){
            if(TextUtils.isEmpty(guidePage.pageTag)){
                throw new RuntimeException("the guide page must set page tag");
            }
            guidePage.setLayoutView();
            guidePage.setKnowEvent();
            guidePage.setNextEvent();
            guidePage.setCloseOnTouchOutside();
            return guidePage;
        }
    }

//    private GuideView guideView;
//    /**
//     *自己控制显示下一个高亮
//     */
//    private void showClickDecoToNext(){
//
//        View deco_view1 = LayoutInflater.from(activity).inflate(layoutId, (ViewGroup) activity.getWindow().getDecorView(), false);
//
//        ((TextView)deco_view1.findViewById(R.id.highest_001)).setText("右上布局\n点击下一个");
//
//        //注意这里要是addView第一个参数传的是View
//        // 一定注意LayoutInflater.from(this).inflate中第二个一定要传入个ViewGroup
//        //为了保存XML中的LayoutParams
////        helper = new GuideViewHelper(activity)
////                .addView(deco_view1.findViewById(highLightView),null)
////                .type(LightType.Circle)
////                .onDismiss(new GuideView.OnDismissListener() {
////                    @Override
////                    public void dismiss() {
//////                        Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_LONG).show();
////                    }
////                });
////        helper.show();
//        guideView = new GuideView(activity);
//        guideView.type(lightType);
//        layoutStyle.showDecorationOnScreen(obtainViewInfo(highLightView), guideView);
//        List<LayoutStyle> layoutStyles = new ArrayList<>();
//        layoutStyles.add(layoutStyle);
//        guideView.setLayoutStyles(layoutStyles);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        rootLayout.addView(guideView, params);
//    }

//    private ViewInfo obtainViewInfo(View view) {
//        int[] loc = new int[2];
//        view.getLocationOnScreen(loc);
//        ViewInfo info = new ViewInfo();
//        switch (lightType) {
//            case Oval:
//            case Rectangle:
//                info.offsetX = loc[0] - padding;
//                info.offsetY = loc[1] - padding;
//                info.width = view.getWidth() + 2 * padding;
//                info.height = view.getHeight() + 2 * padding;
//                break;
//            case Circle:
//                int diameter = Math.max(view.getWidth() + 2 * padding, view.getHeight() + 2 * padding);
//                info.width = diameter;
//                info.height = diameter;
//                info.offsetX = loc[0] - padding;
//                info.offsetY = loc[1] - padding - (diameter / 2 - view.getHeight() / 2 - padding);
//                break;
//        }
//        return info;
//    }

    public void setLayoutView(){
        rootLayout = (FrameLayout) activity.findViewById(android.R.id.content);
        layoutView = View.inflate(activity, layoutId, null);
    }

    public void setKnowEvent(){
        if(layoutView!=null && knowViewId != 0) {
            layoutView.findViewById(knowViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }
    }

    public void setNextEvent(){
        if(layoutView!=null && nextViewId != 0) {
            layoutView.findViewById(nextViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos ++ ;
                   if (nextBack != null){
                       nextBack.onNextBack(layoutView,pos);
                   }
                }
            });
        }
    }

    public void setCloseOnTouchOutside(){
        if(layoutView == null)
            return;
        layoutView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mCancel){
                    cancel();
                }
                return true;  //消费事件，不下发
            }
        });
    }

    public void apply(){
//        showClickDecoToNext();
        rootLayout.addView(layoutView);
    }

    public void cancel(){
        if(rootLayout!=null && layoutView!=null) {
            rootLayout.removeView(layoutView);
            GuidePageManager.setHasShowedGuidePage(activity, pageTag, true);
        }
    }

    public NextBack nextBack;
    private void onNextBack(NextBack nextBack){
        this.nextBack = nextBack;
    }
    public interface NextBack{
        void onNextBack(View rootLayout,int pos);
    }
}
