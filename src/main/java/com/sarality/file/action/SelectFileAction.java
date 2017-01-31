package com.sarality.file.action;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.sarality.action.ActionContext;
import com.sarality.action.ViewAction;

/**
 * Starts a File Chooser Dialog.
 *
 * @author abhideep@ (Abhideep Singh)
 */
public class SelectFileAction implements ViewAction {

  private final Activity activity;
  private final String fileType;
  private final int titleResourceId;
  private final int resultCode;
  private final boolean allowMultiple;

  public SelectFileAction(Activity activity, String fileType, int titleResourceId, int resultCode) {
    this(activity, fileType, titleResourceId, resultCode, false);
  }

  public SelectFileAction(Activity activity, String fileType, int titleResourceId, int resultCode, boolean
      allowMultiple) {
    this.activity = activity;
    this.fileType = fileType;
    this.titleResourceId = titleResourceId;
    this.resultCode = resultCode;
    this.allowMultiple = allowMultiple;
  }


  @Override
  public boolean perform(ActionContext actionContext) {
    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
    chooseFile.setType(fileType);
    if (allowMultiple && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    }
    chooseFile = Intent.createChooser(chooseFile, activity.getResources().getString(titleResourceId));
    activity.startActivityForResult(chooseFile, resultCode);
    return true;
  }
}
