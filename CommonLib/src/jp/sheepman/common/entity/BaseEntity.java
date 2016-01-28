package jp.sheepman.common.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class BaseEntity {

    //最大値を取る対象のカラム
    @Retention(RetentionPolicy.RUNTIME)
    @Target( { ElementType.FIELD })
    public @interface MaxIdTargetColumn{}

	//DBアクセスで無視するアノテーション
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.METHOD })
	public @interface IgnoreDBAccess{}
}
