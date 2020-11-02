/*
 * @(#)BusinessType.java Created on 2012-3-9 下午4:23:32
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.dic;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 * 
 */
public abstract class PastHistoryCode {

	// **既往史类别--过敏物质**
	public static final String ALLERGIC = "01";
	// **既往史类别--疾病史**
	public static final String SCREEN = "02";
	// **既往史类别--手术史**
	public static final String OPERATION = "03";
	// **既往史类别--输血史**
	public static final String TRANSFUSION = "04";
	// **既往史类别--遗传病史**
	public static final String HEREDOPTHIA = "05";
	// **既往史类别--父亲**
	public static final String FATHER = "07";
	// **既往史类别--母亲**
	public static final String MOTHER = "08";
	// **既往史类别--兄弟姐妹**
	public static final String BROTHER = "09";
	// **既往史类别--子女**
	public static final String CHILDREN = "10";
	// **既往史类别--残疾**
	public static final String DEFORMITY = "11";
	// **既往史类别--暴露史**
	public static final String EXPOSE = "12";
	// **既往史--过敏--编码--其他过敏物质**
	public static final String PASTHIS_ALLERGIC_CODE = "0109";
	// **既往史类别--疾病史--无**
	public static final String PASTHIS_SCREEN_NOT_HAVE = "0201";
	// **既往史类别--疾病史-- 心脏病**
	public static final String PASTHIS_SCREEN_CARDIOPATHY = "0204";
	// **既往史类别--疾病史--肾脏疾病**
	public static final String PASTHIS_SCREEN_RENAL = "0213";
	// **既往史类别--疾病史--肝脏疾病**
	public static final String PASTHIS_SCREEN_LIVER = "0210";
	// **既往史类别--疾病史--贫血**
	public static final String PASTHIS_SCREEN_ANAEMIA = "0214";
	// **既往史类别--疾病史--高血压**
	public static final String PASTHIS_SCREEN_HYPERTENSION = "0202";
	// **既往史类别--疾病史--糖尿病**
	public static final String PASTHIS_SCREEN_DIABETES = "0203";
	// **既往史类别--手术史--编码--有手术史
	public static final String PASTHIS_OPERATION_EXISTENT = "0302";
	// **既往史类别--输血史--编码--有输血史
	public static final String PASTHIS_TRANSFUSION_EXISTENT = "0402";
	// **既往史类别--输血史--编码--有遗传病史
	public static final String PASTHIS_HEREDOPTHIA_EXISTENT = "0502";
	// **既往史--疾病史--编码--其他疾病**
	public static final String PASTHIS_SCREEN_OTHER = "0299";
	// **既往史--疾病史--编码--重性精神疾病**
	public static final String PASTHIS_PSYCHOSIS_CODE = "0208";
	// **既往史--遗传病史--编码--有遗传病史**
	public static final String PASTHIS_HEREDOPTHIA_CODE = "0502";
	// **既往史--家族疾病史-父亲--编码--糖尿病**
	public static final String PASTHIS_FATHER_DIABETES = "0703";
	// **既往史--家族疾病史-父亲--编码--重性精神疾病**
	public static final String PASTHIS_FATHER_PSYCHOSIS = "0708";
	// **既往史--家族疾病史-父亲--编码--其他**
	public static final String PASTHIS_FATHER_OTHER = "0799";
	// **既往史--家族疾病史-母亲--编码--糖尿病**
	public static final String PASTHIS_MOTHER_DIABETES = "0803";
	// **既往史--家族疾病史-母亲--编码--重性精神疾病**
	public static final String PASTHIS_MOTHER_PSYCHOSIS = "0808";
	// **既往史--家族疾病史-母亲--编码--其他**
	public static final String PASTHIS_MOTHER_OTHER = "0899";
	// **既往史--家族疾病史-兄弟姐妹--编码--糖尿病**
	public static final String PASTHIS_BROTHER_DIABETES = "0903";
	// **既往史--家族疾病史-兄弟姐妹--编码--重性精神疾病**
	public static final String PASTHIS_BROTHER_PSYCHOSIS = "0908";
	// **既往史--家族疾病史-兄弟姐妹--编码--其他**
	public static final String PASTHIS_BROTHER_OTHER = "0999";
	// **既往史--家族疾病史-子女--编码--糖尿病**
	public static final String PASTHIS_CHILDREN_DIABETES = "1003";
	// **既往史--家族疾病史-子女--编码--重性精神疾病**
	public static final String PASTHIS_CHILDREN_PSYCHOSIS = "1008";
	// **既往史--家族疾病史-子女--编码--其他**
	public static final String PASTHIS_CHILDREN_OTHER = "1099";
	// **既往史--残疾--编码--无残疾**
	public static final String PASTHIS_NOTDEFORMITY_CODE = "1101";
	// **既往史--残疾--编码--肢体残疾**
	public static final String PASTHIS_LIMBDEFORMITY_CODE = "1105";
	// **既往史--残疾--编码--智力残疾**
	public static final String PASTHIS_INTELLIGENCEDEFORMITY_CODE = "1106";
	// **既往史--残疾--编码--脑瘫残疾**
	public static final String PASTHIS_BRAINDEFORMITY_CODE = "1109";
	// **既往史--残疾--编码--其他残疾**
	public static final String PASTHIS_DEFORMITY_OTHER = "1199";
	// **既往史--暴露史--编码--毒物**
	public static final String PASTHIS_POISON_CODE = "1203";
	// **既往史--暴露史--编码--射线**
	public static final String PASTHIS_RADIAL_CODE = "1204";

	// **既往史--无既往史--编码
	public static final String DISEASE_FREE = "01";
}
