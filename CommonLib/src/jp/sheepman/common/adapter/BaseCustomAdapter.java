package jp.sheepman.common.adapter;

import java.util.ArrayList;
import java.util.List;

import jp.sheepman.common.form.BaseForm;
import android.content.Context;
import android.widget.BaseAdapter;

public abstract class BaseCustomAdapter extends BaseAdapter {
    protected List<BaseForm> list;
    public BaseCustomAdapter() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * コンストラクタ
	 * @param context
	 */
	public BaseCustomAdapter(Context context) {
		this(context, new ArrayList<BaseForm>());
	}
	
	/**
	 * コンストラクタ
	 * @param context
	 * @param list	List<EventListForm>
	 */
	public BaseCustomAdapter(Context context, List<BaseForm> list) {
		this.list = list;
	}
    
	/**
	 * アイテムのリストをセットする
	 * @param list
	 */
	public void setList(List<BaseForm> list){
		if(list == null){
			list = new ArrayList<BaseForm>();
		}
		this.list = list;
	}
	
	/**
	 * リストにデータを追加する
	 * @param data	EventListForm
	 */
	public void add(BaseForm data){
		list.add(data);
	}
	
	/**
	 * リストの指定のデータを削除
	 * @param position
	 */
	public void remove(int position){
		list.remove(position);
	}
	
	/**
	 * データを削除する
	 */
	public void clear(){
		list.clear();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
