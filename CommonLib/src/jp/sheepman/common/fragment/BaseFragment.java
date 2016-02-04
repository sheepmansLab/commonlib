package jp.sheepman.common.fragment;

import jp.sheepman.common.form.BaseForm;
import android.app.Fragment;

public abstract class BaseFragment extends Fragment {
	public abstract void callback(); 
	public abstract void callback(BaseForm form); 
}
