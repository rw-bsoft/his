<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_BRDA" tableName="MS_BRDA"  alias="病人档案表" sort="a.XGSJ desc">
	<item id="BRID" alias="病人ID号" type="string" length="18" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="10" startPos="1"/>
		</key>
	</item>
	<item id="EMPIID" alias="EMPIID" type="string" length="32" display="0" />
	<item id="MZHM" alias="门诊号码" type="string" length="32" queryable="true" width="120"/>
	<item id="BRXM" alias="病人姓名" type="string" length="40" queryable="true" selected="true" />
	<item id="FYZH" alias="医疗证号" type="string" length="20" display="0"/>
	<item id="BRXZ" alias="病人性质" length="18"  type="string">
		<dic id="phis.dictionary.patientProperties"/>
	</item>
	<item id="BRXB" alias="病人性别" length="4"  type="string">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="CSNY" alias="出生年月" type="date"/>
	<item id="SFZH" alias="身份证号" type="string" length="20" width="180" display="0"/>
	<item ref="b.idCard" display="1" queryable="true" />
	<item id="HYZK" alias="婚姻状况" length="4" display="0"  type="string"/>
	<item id="ZYDM" alias="职业代码" length="4" display="0"  type="string"/>
	<item id="MZDM" alias="民族代码" length="4" display="0"  type="string"/>
	<item id="XXDM" alias="血型代码" length="4" display="0"  type="string"/>
	<item id="GMYW" alias="过敏药物" length="6" display="0"  type="string"/>
	<item id="DWXH" alias="单位序号" length="18" display="0"  type="string"/>
	<item id="DWMC" alias="单位名称" type="string" length="40" display="0"/>
	<item id="DWDH" alias="单位电话" type="string" length="16" display="0"/>
	<item id="DWYB" alias="单位邮编" type="string" length="6" display="0"/>
	<item id="HKDZ" alias="户口地址" type="string" length="40" display="0"/>
	<item id="JTDH" alias="家庭电话" type="string" length="16" display="0"/>
	<item id="HKYB" alias="户口邮编" type="string" length="6" display="0"/>
	<item id="JZCS" alias="就诊次数" length="4" display="0" type="string"/>
	<item id="JZRQ" alias="就诊日期" type="timestamp" display="0"/>
	<item id="CZRQ" alias="初诊日期" type="timestamp" display="0"/>
	<item id="JZKH" alias="就诊卡号" type="string" length="40" display="0"/>
	<item id="SFDM" alias="省份代码" length="4" display="0" type="string"/>
	<item id="JGDM" alias="籍贯代码" length="4" display="0"  type="string"/>
	<item id="GJDM" alias="国籍代码" length="4" display="0"  type="string"/>
	<item id="LXRM" alias="联系人名" type="string" length="10" display="0"/>
	<item id="LXGX" alias="联系关系" length="4" display="0"  type="string"/>
	<item id="LXDZ" alias="联系地址" type="string" length="40" display="0"/>
	<item id="LXDH" alias="联系电话" type="string" length="16" display="0"/>
	<item id="DBRM" alias="担保人名" type="string" length="10" display="0"/>
	<item id="DBGX" alias="担保关系" length="4" display="0"  type="string"/>
	<item id="SBHM" alias="社保号码" type="string" length="20" width="150" display="0"/>
	<item id="YBKH" alias="医保卡号" type="string" length="20" display="0"/>
	<item id="ZZTX" alias="在职退休" length="4" display="0"  type="string"/>
	<item id="JDJG" alias="建档机构" length="8" not-null="1" width="180"  type="string">
		<dic id="phis.@manageUnit"/>
	</item>
	<item id="JDSJ" alias="建档时间" type="timestamp" width="150"/>
	<item id="JDR" alias="建档人" type="string" length="10"  >
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZXBZ" alias="注销标志" length="1" display="0"  type="string"/>
	<item id="ZXR" alias="注销人" type="string" length="10" display="0"/>
	<item id="ZXSJ" alias="注销时间" type="timestamp" display="0"/>
	<item id="XGSJ" alias="修改时间" type="timestamp" not-null="1" width="150"/>
	<item id="CSD_SQS" alias="出生地_省区市" length="18" display="0"  type="string"/>
	<item id="CSD_S" alias="出生地_市" length="18" display="0"  type="string"/>
	<item id="CSD_X" alias="出生地_县" length="18" display="0"  type="string"/>
	<item id="JGDM_SQS" alias="籍贯代码_省区市" length="18" display="0"  type="string"/>
	<item id="JGDM_S" alias="籍贯代码_市" length="18" display="0"  type="string"/>
	<item id="XZZ_SQS" alias="现住址_省区市" length="18" display="0"  type="string"/>
	<item id="XZZ_S" alias="现住址_市" length="18" display="0"  type="string"/>
	<item id="XZZ_X" alias="现住址_县" length="18" display="0"  type="string"/>
	<item id="XZZ_YB" alias="现住址_邮编" type="string" length="20" display="0"/>
	<item id="XZZ_DH" alias="现住址_电话" type="string" length="20" display="0"/>
	<item id="HKDZ_SQS" alias="户口地址_省区市" length="18" display="0"  type="string"/>
	<item id="HKDZ_S" alias="户口地址_市" length="18" display="0"  type="string"/>
	<item id="HKDZ_X" alias="户口地址_县" length="18" display="0"  type="string"/>
	<item id="XZZ_QTDZ" alias="现住址_其他地址" type="string" length="60" display="0"/>
	<item id="HKDZ_QTDZ" alias="户口地址_其他地址" type="string" length="60" display="0"/>
	<item id="ISGP" alias="家医标志" type="string" length="2" display="0" defaultValue="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MPI_DemographicInfo_CIC">
			<join parent="empiId" child="EMPIID"/>
		</relation>
	</relations>
</entry>
