/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sg.com.utrix.skeleton.app.sample.util;

import android.content.Context;
import android.content.pm.PackageManager;

import sg.com.utrix.skeleton.app.sample.Config;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGD;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGE;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.makeLogTag;

public class NetUtils {
    private static final String TAG = makeLogTag(NetUtils.class);
    private static String mUserAgent = null;

    public static String getUserAgent(Context mContext) {
        if (mUserAgent == null) {
            mUserAgent = Config.APP_NAME;
            try {
                String packageName = mContext.getPackageName();
                String version = mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
                mUserAgent = mUserAgent + " (" + packageName + "/" + version + ")";
                LOGD(TAG, "User agent set to: " + mUserAgent);
            } catch (PackageManager.NameNotFoundException e) {
                LOGE(TAG, "Unable to find self by package name", e);
            }
        }
        return mUserAgent;
    }
}
