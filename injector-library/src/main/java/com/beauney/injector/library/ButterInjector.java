package com.beauney.injector.library;

import android.app.Activity;

/**
 * @author zengjiantao
 * @since 2020-08-20
 */
public class ButterInjector {
    public static void bind(Activity activity) {
        String className = activity.getClass().getName();
        try {
            Class<?> viewBinderClass = Class.forName(className + "$$ViewBinder");
            ViewBinder viewBinder = (ViewBinder) viewBinderClass.newInstance();
            viewBinder.bind(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
