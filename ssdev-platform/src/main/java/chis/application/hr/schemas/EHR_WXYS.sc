<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_WXYS" alias="危险因素" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="sg" alias="身高(cm)" type="string" group="体检情况"/>
	<item id="tz" alias="体重(kg)" type="string" group="体检情况"/>
	<item id="yw" alias="腰围(cm)" type="string" group="体检情况"/>
	<item id="tw" alias="臀围(cm)" type="string" group="体检情况"/>
	<item id="ssy" alias="收缩压(mmHg)" type="string" group="体检情况"/>
	<item id="szy" alias="舒张压(mmHg)" type="string" group="体检情况"/>
	
	<item id="jwsqk" alias="既往史情况" type="string" colspan="3" group="既往史情况">
		<dic render="LovCombo">
			<item key="1" text="高血压"/>
			<item key="2" text="高血脂"/>
			<item key="3" text="冠心病"/>
			<item key="4" text="卒中"/>
			<item key="5" text="乳腺癌"/>
			<item key="6" text="糖尿病"/>
			<item key="7" text="其他心脏病"/>
			<item key="8" text="不详"/>
		</dic>
	</item>
	<item id="jzsqk" alias="家庭史情况" type="string" colspan="3" group="家庭史情况">
		<dic render="LovCombo">
			<item key="1" text="糖尿病家族历史"/>
			<item key="2" text="高血压家族史"/>
			<item key="3" text="冠心病家族史"/>
		</dic>
	</item>
	<item id="ybrgx1" alias="与本人关系" type="string" colspan="2" group="肿瘤家族史"/>
	<item id="zlmc1" alias="肿瘤名称" type="string" colspan="2" group="肿瘤家族史"/>
	<item id="ybrgx2" alias="与本人关系" type="string" colspan="2" group="肿瘤家族史"/>
	<item id="zlmc2" alias="肿瘤名称" type="string" colspan="2" group="肿瘤家族史"/>
	<item id="ybrgx3" alias="与本人关系" type="string" colspan="2" group="肿瘤家族史"/>
	<item id="zlmc3" alias="肿瘤名称" type="string" colspan="2" group="肿瘤家族史"/>
	
	<item id="xypl" alias="吸烟频率" type="string" group="吸烟情况">
		<dic>
			<item key="1" text="从不吸"/>
			<item key="2" text="现在吸，但不是每天吸"/>
			<item key="3" text="过去吸，现在不吸"/>
			<item key="4" text="现在每天吸"/>
		</dic>
	</item>
	<item id="ksxynl" alias="开始吸烟年龄(岁)" type="string" group="吸烟情况"/>
	<item id="ksmtxynl" alias="开始每天吸烟年龄(岁)" type="string" group="吸烟情况"/>
	<item id="rixylz" alias="日吸烟量(支)" type="string" group="吸烟情况"/>
	<item id="ysjyjy" alias="医生建议戒烟" type="string" group="吸烟情况">
		<dic render="Radio" colWidth="50">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="tzxysc" alias="停止吸烟时长(天)" type="string" group="吸烟情况"/>
	<item id="bdxy" alias="被动吸烟" type="string" group="吸烟情况">
		<dic render="Radio" colWidth="50">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="bdxycslb" alias="被动吸烟场所类别" type="string" group="吸烟情况"/>
	
	<item id="sfyj" alias="是否饮酒" type="string" group="饮酒情况">
		<dic>
			<item key="1" text="经常饮酒"/>
			<item key="2" text="偶尔"/>
			<item key="3" text="不饮酒"/>
		</dic>
	</item>
	<item id="yjpl" alias="饮酒频率(次/周)" type="string" group="饮酒情况"/>
	<item id="glyj" alias="过量饮酒" type="string" group="饮酒情况">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="ysjyjj" alias="医生建议戒酒" type="string" group="饮酒情况">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="sfyglhd" alias="是否有规律活动" type="string" group="运动情况">
		<dic>
			<item key="1" text="偶尔"/>
			<item key="2" text="规律"/>
			<item key="3" text="不锻炼"/>
		</dic>
	</item>
	<item id="glhdzl" alias="规律活动种类" type="string" colspan="2" group="运动情况"/>
	<item id="glhdpl" alias="规律活动频率(次/周)" type="string" colspan="1" group="运动情况"/>
	<item id="mcgl" alias="每次规律活动持续时间(分钟)" type="string" colspan="2" group="运动情况"/>
	
	<item id="sfssgy" alias="是否膳食高盐" type="string" group="膳食情况">
		<dic>
			<item key="1" text="高盐"/>
			<item key="2" text="一般量盐"/>
			<item key="3" text="少量盐"/>
			<item key="3" text="不放盐"/>
		</dic>
	</item>
	
	<item id="lryg" alias="录入员工" type="string" group="录入记录"/>
	<item id="lrrq" alias="录入日期" type="datetime" xtype="datefield" group="录入记录"/>
	<item id="czrq" alias="操作日期" type="datetime" xtype="datefield" group="录入记录"/>
	
</entry>
