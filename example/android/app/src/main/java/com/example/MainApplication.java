package com.example;

import android.app.Application;

import com.facebook.react.ReactApplication;
import io.hengam.RNHengamPackage;

import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
            new RNHengamPackage()
      );

//      @SuppressWarnings("UnnecessaryLocalVariable")
//      List<ReactPackage> packages = new PackageList(this).getPackages();
//      // Packages that cannot be autolinked yet can be added manually here, for example:
//      // packages.add(new MyReactNativePackage());
//      return packages;
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    io.hengam.RNHengam.initializeEventListeners(this);

  }
}
