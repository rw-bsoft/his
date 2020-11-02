<?xml version="1.0" encoding="UTF-8"?>
<entry alias="高血压档案">
	<item id="gllb" alias="管理类别" type="string"/>
	<item id="fz" alias="分组" type="string">
		<dic>
			<item key="1" text="一组"/>
			<item key="2" text="二组"/>
			<item key="3" text="三组"/>
		</dic>
	</item>
	<item id="xxly" alias="信息来源" type="string">
		<dic id="chis.dictionary.hypertensionRecordSource" onlySelectLeaf="true"/>
	</item>
	<item id="grbm" alias="个人编码" type="string" />
	<item id="fwzx" alias="服务中心" type="string"/>
	<item id="jw" alias="居委" type="string"/>
	<item id="bh" alias="编号" type="string"/>
	
	<item id="xm" alias="姓名" type="string" group="基本信息"/>
	<item id="sexCode" alias="性别" type="string" group="基本信息">
		<dic id="chis.dictionary.gender" onlySelectLeaf="true"/>
	</item>
	<item id="birthday" alias="出生日期" type="datetime" xtype="datefield" not-null="1" group="基本信息"/>
	<item id="csny" alias="职业" type="string" group="基本信息">
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true"/>
	</item>
	<item id="lxdh" alias="联系电话" type="string" group="基本信息"/>
	<item id="yb" alias="邮编" type="string" group="基本信息"/>
	<item id="jtdz" alias="家庭地址" colspan="2" type="string" group="基本信息"/>
	<item id="hklx" alias="户口类型" type="string" group="基本信息">
		<dic id="chis.dictionary.registerType" onlySelectLeaf="true"/>
	</item>
	<item id="jkrq" alias="建卡日期" type="datetime" xtype="datefield" not-null="1" group="基本信息"/>
	<item id="zrys" alias="责任医师" type="string" group="基本信息"/>
	
	<item id="cjzyy" alias="常就诊医院" colspan="4" type="string" group="就诊/家庭史">
		<dic render="Radio" colWidth="160">
			<item key="1" text="本院"/>
			<item key="2" text="其他一级医院"/>
			<item key="3" text="本区二、三级医院"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item id="jzs" alias="家族史" colspan="3" type="string" group="就诊/家庭史">
		<dic id="chis.dictionary.familyHistroy" render="LovCombo"/>
	</item>
	
	<item id="xyqk" alias="吸烟情况" type="string" group="生活习惯">
		<dic id="chis.dictionary.CV5101_24" onlySelectLeaf="true"/>
	</item>
	<item id="yjqk" alias="饮酒情况" type="string" group="生活习惯">
		<dic>
			<item key="1" text="从不"/>
			<item key="2" text="偶尔"/>
			<item key="3" text="经常"/>
			<item key="4" text="每天"/>
		</dic>
	</item>
	<item id="yjgl" alias="饮酒过量" type="string" group="生活习惯">
		<dic id="chis.dictionary.yesOrNo" onlySelectLeaf="true"/>
	</item>
	<item id="tydl" alias="体育锻炼" type="string" group="生活习惯">
		<dic id="chis.dictionary.CV5101_28" render="Tree" onlySelectLeaf="true"/>
	</item>
	
	<item id="sg" alias="身高(cm)" type="string" group="血压情况"/>
	<item id="tz" alias="体重(kg)" type="string" group="血压情况"/>
	<item id="ssy" alias="收缩压(mmHg)" type="string" group="血压情况"/>
	<item id="szy" alias="舒张压(mmHg)" type="string" group="血压情况"/>
	<item id="xjgldx" alias="细节管理对象" type="string" xtype="checkbox" group="血压情况"/>
	<item id="xjglbh" alias="细节管理编号" type="string" group="血压情况"/>
	<item id="wxys" alias="危险因素" colspan="2" type="string" group="血压情况">
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo"/>
	</item>
	<item id="bqgsh" alias="靶器官损害" colspan="2" type="string" group="血压情况">
		<dic id="chis.dictionary.targetHurt" render="LovCombo"/>
	</item>
	<item id="bfz" alias="并发症" colspan="2" type="string" group="血压情况">
		<dic id="chis.dictionary.complication" render="LovCombo"/>
	</item>
	
	<item id="shzlnl" alias="生活自理能力" type="string" group="其他">
		<dic id="chis.dictionary.oldSelfCareGrade"/>
	</item>
	<item id="jsgjkjy" alias="接受过健康教育" type="string" group="其他">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="zzglrq" alias="终止管理日期" type="datetime" xtype="datefield" not-null="1" group="其他"/>
	<item id="zzly" alias="终止理由" type="string" group="其他"/>
	<item id="bltl" alias="病例讨论" type="string" colspan="4" group="其他"/>
	<item id="tlyy" alias="讨论原因" type="string" colspan="4" group="其他"/>
	<item id="tljg" alias="讨论结果" type="string" colspan="4" group="其他"/>
</entry>