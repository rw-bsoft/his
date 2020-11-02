<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_BCJL_RZ" tableName="YS_MZ_JZLS" alias="门诊病历(病程记录)" sort="a.KSSJ desc">
	<item id="JZXH" alias="就诊序号" length="18" display="0" type="long" not-null="1"/>
	<item id="BRBH" alias="病人ID" type="long" display="0" length="18"/>
	<item id="GHXH" alias="挂号序号" type="long" display="0" length="18"/>
	<item id="KSSJ" alias="就诊时间" type="timestamp" not-null="1" width="80"/>
	<item id="JSSJ" alias="结束时间" type="timestamp" width="140" display="0"/>
	<item ref="c.BRXM" alias="姓名" type="long" length="18" width="50"/>
	<item ref="c.BRXB" alias="性别" type="long" length="5" width="40"/>
	<item ref="c.SFZH" alias="身份证号" type="String" length="20" width="140"/>
	<item ref="c.ZYDM" alias="职业" type="long" length="100" width="200">
	</item>	
	<item ref="e.FZBZ" alias="初/复诊">	    
	</item>
	<item id="age" alias="年龄" type="string" renderer="onRenderer" width="40" length="10" virtual="true"/>
	<item ref="e.FBRQ" alias="发病日期" type="date" width="80"/>
	<item ref="e.ZDMC" alias="主要诊断" length="18" width="150"/>
	
	<item ref="c.LXDZ" alias="联系地址" type="string" length="240" width="200"/>
	<item ref="c.DWMC" alias="单位名称" type="string" length="200" width="150"/>
	<item ref="c.JTDH" alias="联系电话" type="string" length="50" width="100"/>
	<item ref="d.CZPB" alias="门诊类型" type="int" length="4"/>
	<item ref="b.JZKS" alias="就诊科室" length="18" type="long" display="0" />
	<item ref="b.JZYS" alias="就诊医生" type="string" length="10" display="0"/>
	<item ref="b.ZSXX" alias="主诉信息" type="string" length="1000" display="0"/>
	<item ref="b.XBS" alias="现病史" type="string" length="1000" display="0"/>
	<item ref="b.JWS" alias="既往史" type="string" length="1000" display="0"/>
	<item ref="b.TGJC" alias="体格检查" type="string" length="1000" display="0"/>
	<item ref="b.FZJC" alias="辅助检查" type="string" length="1000" display="0"/>
	<item ref="b.CLCS" alias="处理措施" type="string" display="0" length="1000"/>
	<item ref="b.T" alias="体温" type="double" length="3" precision="1" display="0"/>
	<item ref="b.P" alias="脉搏" type="int" length="4" display="0"/>
	<item ref="b.R" alias="呼吸" type="int" length="4" display="0"/>
	<item ref="b.SSY" alias="收缩压" type="int" length="3" width="60" />
	<item ref="b.SZY" alias="舒张压" type="int" length="3" width="60"/>
	<item ref="b.KS" alias="咳嗽" type="int" length="1" display="0"/>
	<item ref="b.YT" alias="咽痛" type="int" length="1" display="0"/>
	<item ref="b.HXKN" alias="呼吸困难" type="int" length="1" display="0"/>
	<item ref="b.OT" alias="呕吐" type="int" length="1" display="0"/>
	<item ref="b.FT" alias="腹痛" type="int" length="1" display="0"/>
	<item ref="b.FX" alias="腹泻" type="int" length="1" display="0"/>
	<item ref="b.PZ" alias="皮疹" type="int" length="1" display="0"/>
	<item ref="b.QT" alias="其他" type="int" length="1" display="0"/>
	<item ref="b.BRQX" alias="病人去向" width="60"/>
	<item ref="b.JKJYNR" alias="健康教育" type="stirng" width="1000" display="0" renderer="jkjyRenderer"/>
	<item ref="c.CSNY" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BCJL" >
			<join parent="JZXH" child="JZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA" >
			<join parent="BRID" child="BRBH"></join>
		</relation>	
		<relation type="parent" entryName="phis.application.cic.schemas.MS_GHMX_MZ" >
			<join parent="SBXH" child="GHXH"></join>
		</relation>	
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRZD_CIC" >
			<join parent="JZXH" child="JZXH"></join>
		</relation>				
	</relations>
</entry>
