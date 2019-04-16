package com.sxhalo.PullCoal.ui.pullrecyclerview.uihelper;

import android.app.Activity;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.BaseView;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.TemplateAutoScroll;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.TemplateBanner;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.TemplateDriver;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.TemplateFineCoalViewPage;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.TemplateFunction;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.TemplateInformationDepartment;


/**
 * 工具模板创建
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateManager
{
    public static int getViewType(String tempId)
    {
        if (tempId == null || tempId.equals(""))
        {
            return -1;
        }
        else if (tempId.equals(TemplateConstant.template_banner))
        {
            return 0;
        }else if (tempId.equals(TemplateConstant.template_function))
        {
            return 1;
        }else if (tempId.equals(TemplateConstant.template_auto_scroll))
        {
            return 2;
        }else if (tempId.equals(TemplateConstant.template_fine_coal))
        {
            return 3;
        }else if (tempId.equals(TemplateConstant.template_information_department))
        {
            return 4;
        }
        else if (tempId.equals(TemplateConstant.template_driver))
        {
            return 5;
        }

        return -2;
    }

    public static BaseView findViewById(Activity context, String tempId)
    {
        if (tempId == null || tempId.equals(""))
        {
            return null;
        }
        if (tempId.equals(TemplateConstant.template_banner))
        {
            return new TemplateBanner(context);
        }
        else if (tempId.equals(TemplateConstant.template_function))
        {
            return new TemplateFunction(context);
        }
        else if (tempId.equals(TemplateConstant.template_auto_scroll))
        {
            return new TemplateAutoScroll(context);
        }
        else if (tempId.equals(TemplateConstant.template_fine_coal))
        {
//            return new TemplateFineCoal(context);
            return new TemplateFineCoalViewPage(context);
        }
        else if (tempId.equals(TemplateConstant.template_information_department))
        {
            return new TemplateInformationDepartment(context);
        }
        else if (tempId.equals(TemplateConstant.template_driver))
        {
            return new TemplateDriver(context);
        }
        return null;
    }

}
