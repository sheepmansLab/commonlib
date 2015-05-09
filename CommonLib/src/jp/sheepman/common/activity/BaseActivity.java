package jp.sheepman.common.activity;

import jp.sheepman.common.form.BaseForm;
import android.app.Activity;

public abstract class BaseActivity extends Activity {
	public abstract void callback(); 
	public abstract void callback(BaseForm form); 
}
