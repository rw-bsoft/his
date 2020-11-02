<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GHMX_MZ" tableName="MS_GHMX" alias="挂号明细表" sort="a.PLXH,a.JZHM">
	<item id="SBXH" alias="识别序号" type="long" length="18" hidden="true" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" length="8" hidden="true" type="string" />
	<item id="BRID" alias="病人ID号" length="18" hidden="true" type="string" />
	<item id="PLXH" alias="排列序号" type="long" length="18"/>
	<item id="JZHM" alias="就诊号码"  type="string" length="20" width="120"/>
	<item ref="b.EMPIID" alias="empiId" type="string"  length="32" display="0" />
	<item ref="b.JZKH" alias="卡号" type="string" length="20" width="110" queryable="true"/>
	<item ref="b.MZHM" alias="门诊号码" type="string"  length="32" width="110" not-null="1"/>
	<item ref="b.BRXZ" alias="病人性质" type="string" />
	<item ref="b.BRXM" alias="姓名" type="string"  length="40" queryable="true" selected="true"/>
	<item ref="b.BRXB" alias="性别" type="string" length="4" />
	<item ref="b.CSNY" alias="出生年月" type="string" />
    <item ref="b.SFZH" alias="身份证号" type="string" length="20" display="0"/>
	<item id="HOWOLD" alias="年龄"  type="string" length="20" width="60"/>
	<item id="GHSJ" alias="挂号时间" width="140" defaultValue="%server.date.date" type="datetime"/>
    <item id="YSDM" alias="就诊医生"  type="string" length="20" width="80">
    	<dic id="phis.dictionary.doctor"/>
    </item>
    
    <item id="SIGNFLAG" alias="是否签约" type="string" length="1" display="1" update="false" fixed="true" renderer="showColor">
    <dic>
			<item key="0" text="未签约"/>
			<item key="1" text="已签约"/>
	</dic>
	</item>
	<item id="SCEENDDATE" alias="签约到期时间" display="1" queryable="true"/>
    <item id="CZPB" alias="门诊类型" type="int" display="0" length="4">
	    <dic>
		   <item key="1" text="初诊"/>
		   <item key="0" text="复诊"/>
	    </dic>
    </item>
	<item id="JZZT" alias="就诊状态" length="1" hidden="true" type="int" />
	<item id="GRDA" alias="个人档案" length="1" hidden="true" type="int" />
	<item id="GXYDA" alias="高血压档案" length="1" hidden="true" type="int" />
	<item id="TNBDA" alias="糖尿病档案" length="1" hidden="true" type="int" />
	<item id="GXYZD" alias="高血压诊断" length="1" hidden="true" type="int" />
	<item id="TNBZD" alias="糖尿病诊断" length="1" hidden="true" type="int" />
	<item id="operate" alias="档案管理"  type="string" width="200" renderer="onRenderer_btn"/>
	
	
	<relations>
		<relation type="children" entryName="phis.application.cic.schemas.MS_BRDA" >
			<join parent="BRID" child="BRID" />
		</relation>
	</relations>
	
</entry>
