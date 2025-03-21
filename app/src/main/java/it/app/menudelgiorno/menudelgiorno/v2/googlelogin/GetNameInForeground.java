/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.app.menudelgiorno.menudelgiorno.v2.googlelogin;


import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * This example shows how to fetch tokens if you are creating a foreground task/activity and handle
 * auth exceptions.
 */
public class GetNameInForeground extends AbstractGetNameTask {

  public GetNameInForeground(GoogleLogin activity, String email, String scope) {
      super(activity, email, scope);
  }


  /**
   * Get a authentication token if one is not available. If the error is not recoverable then
   * it displays the error message on parent activity right away.
   */
  @Override
  protected String fetchToken() throws IOException {
      try {
          return GoogleAuthUtil.getToken(mActivity.getActivity(), mEmail, mScope);
      } catch (UserRecoverableAuthException userRecoverableException) {
          // GooglePlayServices.apk is either old, disabled, or not present, which is
          // recoverable, so we need to show the user some UI through the activity.
          mActivity.handleException(userRecoverableException);
      } catch (GoogleAuthException fatalException) {
          onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
      }
      return null;
  }
}
