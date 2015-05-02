package jp.sheepman.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.model.BaseModel;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB操作のユーティリティ
 *
 */
public class DatabaseUtil {
	private final String DIR_DB_PROPS = "sql";
	private final String FILE_DB_PROP = "database.properties";
	private final String DB_NAME_DEFAULT = "database.sqlite";
	private final String KEYWORD_GETTER = "get";
	private final String KEYWORD_SETTER = "set";
	//SQLiteOpenHelperのカスタムクラス
	private DatabaseHelper helper;
	//DB接続
	private SQLiteDatabase db;
	
	/**
	 * コンストラクタ。ヘルパークラスのインスタンスを取得する。
	 * @param context
	 */
	public DatabaseUtil(Context context) {
		//DB名にデフォルトをセット
		String DB_NAME = DB_NAME_DEFAULT;
		//assetから情報を取得する
		AssetManager as = context.getAssets();
		BufferedReader br = null;
		try{
			//指定ディレクトリ配下のファイルを取得
			String[] files = as.list(DIR_DB_PROPS);
			for(String f : files){
				//指定ファイルであれば読み込み
				if(f.equals(FILE_DB_PROP)){
					//ファイルをオープンする
					br = new BufferedReader(new InputStreamReader(as.open(DIR_DB_PROPS+"/"+f), "UTF-8"));
					//1行のみ読み込み TODO 複数設定対応
					String str = br.readLine();
					//空でなければセット
					if(str != null){
						DB_NAME = str;
					}
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null ){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		helper = new DatabaseHelper(DB_NAME, context);
	}


	
	/**
	 * データベースのオープン
	 */
	public void open(){
		//DBの接続
		db = helper.getWritableDatabase();
	}
	
	/**
	 * データベースのクローズ
	 */
	public void close(){
		//DBHelperのクローズ。DBの接続もクローズしてくれる。
		helper.close();
	}
	
	/**
	 * Insert処理
	 * @param entity
	 */
	public void insert(String tablename, BaseEntity entity){
		ContentValues values = new ContentValues();
		try {
			//Entityクラスのメソッド一覧を取得
			Method[] methods = entity.getClass().getDeclaredMethods();
			for(Method m : methods){
				//getterメソッドのみ扱う
				if(m.getName().startsWith(KEYWORD_GETTER)){
					//IgnoreDBAccessアノテーションが付いていたら無視する
					if(m.getAnnotation(BaseEntity.IgnoreDBAccess.class) == null){
						//カラム名であるkey項目はgetterメソッドのgetを外して小文字にしたもの
						String key = m.getName().replaceFirst(KEYWORD_GETTER, "").toLowerCase(Locale.ENGLISH);
						//getterメソッドを実行して値を取得する
						Object value = m.invoke(entity, null);
						//値がNULLでない場合、型別にキャストしてセットする
						if(value != null){
							if(value instanceof byte[]){
								values.put(key, (byte[])value);
							} else if(value instanceof Byte){
								values.put(key, (Byte)value);
							} else if(value instanceof Boolean){
								values.put(key, (Boolean)value);
							} else if(value instanceof Double){
								values.put(key, (Double)value);
							} else if(value instanceof Float){
								values.put(key, (Float)value);
							} else if(value instanceof Integer){
								values.put(key, (Integer)value);
							} else if(value instanceof Long){
								values.put(key, (Long)value);
							} else if(value instanceof Short){
								values.put(key, (Short)value);
							} else {
								//上記以外は文字列としてセット
								values.put(key, value.toString());
							}
						} else {
							//NULLの場合はNULLをセットする
							values.putNull(key);
						}
					}
				}
			}
			db.insert(tablename, "-", values);
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	/**
	 * delete
	 * @param tablename		テーブル名
	 * @param whereClause	Where句
	 * @param whereArgs		Whereバインド
	 */
	public void delete(String tablename, String whereClause, List<String> list){
		db.delete(tablename, whereClause, list.toArray(new String[0]));
	}
	
	/**
	 * Select
	 * @param sql
	 * @param params
	 * @param model
	 * @return
	 */
	public List<BaseEntity> select(String sql, List<String> params, BaseModel model){
		List<BaseEntity> list = new ArrayList<BaseEntity>();
		try{
			Map<String, Method> map = new HashMap<String, Method>();
			for(Method m : model.getEntity().getClass().getDeclaredMethods()){
				if(m.getName().startsWith(KEYWORD_SETTER)){
					map.put(m.getName()
							.replaceFirst(KEYWORD_SETTER, "")
							.toLowerCase(Locale.ENGLISH)
							, m);
				}
			}
			
			Cursor c = db.rawQuery(sql, params.toArray(new String[0]));
			while(c.moveToNext()){
				BaseEntity entity = model.getEntity();
				for(int i = 0; i < c.getColumnCount(); i++){
					String key = c.getColumnName(i)
							.replaceFirst(KEYWORD_SETTER, "")
							.toLowerCase(Locale.ENGLISH);
					if(map.containsKey(key)){
						Object value;
						if(c.getType(i) == Cursor.FIELD_TYPE_BLOB){
							value = c.getBlob(i);
						} else if(c.getType(i) == Cursor.FIELD_TYPE_FLOAT){
							value = c.getFloat(i);
						} else if(c.getType(i) == Cursor.FIELD_TYPE_INTEGER){
							value = c.getInt(i);
						} else if(c.getType(i) == Cursor.FIELD_TYPE_STRING){
							value = c.getString(i);
						} else if(c.getType(i) == Cursor.FIELD_TYPE_NULL){
							value = null;
						} else {
							value = c.getString(i);
						}
						map.get(key).invoke(entity, value);
					}
				}
				list.add(entity);
			}
			c.close();
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * update
	 * @param sql
	 * @param list
	 */
	public void update(String table, String whereClause, BaseEntity entity, List<String> list){
		ContentValues values = new ContentValues();
		try {
			//Entityクラスのメソッド一覧を取得
			Method[] methods = entity.getClass().getDeclaredMethods();
			for(Method m : methods){
				//getterメソッドのみ扱う
				if(m.getName().startsWith(KEYWORD_GETTER)){
					//IgnoreDBAccessアノテーションが付いていたら無視する
					if(m.getAnnotation(BaseEntity.IgnoreDBAccess.class) == null){
						//カラム名であるkey項目はgetterメソッドのgetを外して小文字にしたもの
						String key = m.getName().replaceFirst(KEYWORD_GETTER, "").toLowerCase(Locale.ENGLISH);
						//getterメソッドを実行して値を取得する
						Object value = m.invoke(entity, null);
						//値がNULLでない場合、型別にキャストしてセットする
						if(value != null){
							if(value instanceof byte[]){
								values.put(key, (byte[])value);
							} else if(value instanceof Byte){
								values.put(key, (Byte)value);
							} else if(value instanceof Boolean){
								values.put(key, (Boolean)value);
							} else if(value instanceof Double){
								values.put(key, (Double)value);
							} else if(value instanceof Float){
								values.put(key, (Float)value);
							} else if(value instanceof Integer){
								values.put(key, (Integer)value);
							} else if(value instanceof Long){
								values.put(key, (Long)value);
							} else if(value instanceof Short){
								values.put(key, (Short)value);
							} else {
								//上記以外は文字列としてセット
								values.put(key, value.toString());
							}
						} else {
							//NULLの場合はNULLをセットする
							values.putNull(key);
						}
					}
				}
			}
			db.update(table, values, whereClause, list.toArray(new String[0]));
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	//sqlite接続用のHelperクラス
	private class DatabaseHelper extends SQLiteOpenHelper {
		private Context context;
		
		/**
		 * コンストラクタ
		 * @param context	Context
		 */
		public DatabaseHelper(String DB_NAME, Context context) {
			// DBの名前は固定
			// CursorFactoryはnull
			// DBのバージョンは1固定
			super(context, DB_NAME, null, 1);
			this.context = context;
		}
		
		/**
		 * DBが存在しない場合に呼ばれるメソッド
		 * @param db SQLiteDatabase
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// テーブルをCreateする
			this.execSQL(db, "sql/create");
		}

		/**
		 * DBのversionが変わった場合に呼びだされるメソッド
		 * @param db SQLiteDatabase
		 * @param oldVersion	変更前のバージョン
		 * @param newVersion	変更後のバージョン
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// とりあえず何もしない
		}
		
		/**
		 * assetのSQLファイルを読み込んで実行する
		 * @param dir	asset配下のディレクトリパス
		 */
		private void execSQL(SQLiteDatabase sqldb ,String dir){
			AssetManager as = context.getAssets();
			try{
				String[] files = as.list(dir);
				for(int i = 0; i < files.length; i ++){
					String str = readFile(as.open(dir + "/" + files[i]));
					for(String sql : str.split("/")){
						if(sql.replaceAll(" ", "").length() > 0){
							sqldb.execSQL(sql);
						}
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}

	    /** 
	     * ファイルから文字列を読み込む 
	     * @param is 
	     * @return ファイルの文字列 
	     * @throws IOException 
	     */  
	    private String readFile(InputStream is) throws IOException{  
	        BufferedReader br = null;  
	        try {  
	            br = new BufferedReader(new InputStreamReader(is,"UTF-8"));  	  
	            StringBuilder sb = new StringBuilder();      
	            String str;        
	            while((str = br.readLine()) != null){        
	                sb.append(str);
	            }      
	            return sb.toString();  
	        } catch(IOException e) {
	        	throw e;
	        } finally {
	            if (br != null) br.close();  
	        }  
	    }  
	}
}
