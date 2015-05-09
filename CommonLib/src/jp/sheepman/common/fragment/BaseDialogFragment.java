package jp.sheepman.common.fragment;

import jp.sheepman.common.form.BaseForm;
import android.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {
	public abstract void callback(); 
	public abstract void callback(BaseForm form); 
}
