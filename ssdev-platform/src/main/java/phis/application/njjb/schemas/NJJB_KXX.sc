<?xml version="1.0" encoding="UTF-8"?>
<entry alias="" entityName="NJJB_KXX" sort="">
<item alias="社会保障卡号" id="SHBZKH" length="20" pkey="true" fixed="true" not-null="true" type="string"/>
<item alias="单位编号" id="DWBH" length="14" fixed="true" type="string"/>
<item alias="单位名称" id="DWMC" length="200" fixed="true" type="string"/>
<item alias="身份证号" id="SFZH" length="18" fixed="true" type="string"/>
<item alias="姓名" id="XM" length="20" fixed="true" type="string"/>
<item alias="性别" id="XB" length="3" fixed="true" type="string">
	<dic id="phis.dictionary.gender" fixed="true" autoLoad="true" />
</item>
<!--
<item alias="医疗类别" id="YLLB" length="3" type="string" hidden="true" >
	<dic id="phis.dictionary.ybyllb" autoLoad="true" render="LovCombo" />
</item>
-->
<item alias="医疗人员类别" id="YLRYLB" length="3" fixed="true" type="string">
	<dic render="LovCombo">
		<item key="11" text="在职"/>
		<item key="21" text="退休"/>
		<item key="22" text="退职"/>
		<item key="23" text="70岁以上退休"/>
		<item key="24" text="退休待审核"/>
		<item key="31" text="离休"/>
		<item key="41" text="建国前老工人"/>
		<item key="42" text="二乙伤残军人"/>
		<item key="51" text="普通居民"/>
		<item key="52" text="儿童学生"/>
		<item key="53" text="大学生"/>
		<item key="62" text="建筑业农民工"/>
	</dic>
</item>
<item alias="异地人员标志" id="YDRYBZ" length="3" fixed="true" type="string"/>
<item alias="统筹区号" id="TCQH" length="20" fixed="true" type="string">
	<dic>
		<item key="320101"  text="市本级"/>
		<item key="320102"  text="玄武区"/>
		<item key="320104"  text="秦淮区"/>
		<item key="320105"  text="建邺区"/>
		<item key="320106"  text="鼓楼区"/>
		<item key="320108"  text="化学工业园区"/>
		<item key="320111"  text="浦口区"/>
		<item key="320113"  text="栖霞区"/>
		<item key="320114"  text="雨花台区"/>
		<item key="320115"  text="江宁区"/>
		<item key="320116"  text="六合区"/>
		<item key="320124"  text="溧水区"/>
		<item key="320125"  text="高淳区"/>
		<item key="320131"  text="经济管委会"/>
		<item key="320132"  text="高新区管委会"/>
		<item key="320133"  text="化工园区管委会"/>
	</dic>
</item>	
<item alias="当前帐户余额" id="DQZHYE" length="16"  not-null="true" fixed="true" type="string"/>
<item alias="在院状态" id="ZYZT" length="3" fixed="true" type="string">
	<dic>
		<item key="0" text="不在院"/>
		<item key="1" text="在院"/>
	</dic>	
</item>
<item alias="本年住院次数" id="BNZYCS" length="3" fixed="true" type="string"/>
<item alias="待遇享受标志" id="DYXSBZ" length="3" fixed="true" type="string">
	<dic>
		<item key="1" text="正常享受"/>
		<item key="2" text="不享受待遇"/>
	</dic>
</item>	
<item alias="待遇不享受原因" id="DYBXSYY" length="100" fixed="true" type="string">
	<dic>
		<item key="02"  text="欠费"/>
		<item key="03"  text="退休待批"/>
		<item key="04"  text="等待期"/>
		<item key="05"  text="退保"/>
		<item key="99"  text="其他特殊情况"/>
	</dic>
</item>	
<item alias="病种登记情况" id="BZDJQK" length="400" fixed="true" type="string"/>
<item alias="医保门慢资格" id="YBMMZG" length="3" fixed="true" type="string"/>
<item alias="医保门慢病种" id="YBMMBZ" length="400" fixed="true" type="string"/>
<item alias="医保门精资格" id="YBMJZG" length="3" fixed="true" type="string"/>
<item alias="医保门精病种" id="YBMJBZ" length="400" fixed="true" type="string"/>
<item alias="医保门艾资格" id="YBMAZG" length="3" fixed="true" type="string"/>
<item alias="医保门艾病种" id="YBMABZ" length="400" fixed="true" type="string"/>
<item alias="医保丙肝干扰素资格" id="YBBGGRSZG" length="3" fixed="true" type="string"/>
<item alias="医保丙肝干扰素病种" id="YBBGGRSBZ" length="400" fixed="true" type="string"/>
<item alias="医保门诊血友病资格" id="YBMZXYBZG" length="3" fixed="true" type="string"/>
<item alias="医保门诊血友病病种" id="YBMZXYBBZ" length="400" fixed="true" type="string"/>
<item alias="医保门特资格" id="YBMTZG" length="3" fixed="true" type="string"/>
<item alias="医保门特病种" id="YBMTBZ" length="400" fixed="true" type="string"/>
<item alias="医保特药资格" id="YBTYZG" length="3" fixed="true" type="string"/>
<item alias="医保特药病种" id="YBTYBZ" length="400" fixed="true" type="string"/>
<item alias="医保特药名称编码" id="YBTYMCBM" length="400" fixed="true" type="string"/>
<item alias="居民门大资格" id="JMMDZG" length="3" fixed="true" type="string"/>
<item alias="居民门大病种" id="JMMDBZ" length="400" fixed="true" type="string"/>
<item alias="居民门诊血友病资格" id="JMMZXYBZG" length="3" fixed="true" type="string"/>
<item alias="居民门诊血友病病种" id="JMMZXYBBZ" length="400" fixed="true" type="string"/>
<item alias="居民特药资格" id="JMTYZG" length="3" fixed="true" type="string"/>
<item alias="居民特药病种" id="JMTYBZ" length="400" fixed="true" type="string"/>
<item alias="居民特药名称编码" id="JMTYMCBM" length="400" fixed="true" type="string"/>
<item alias="农民工门大资格" id="NMGMDZG" length="3" fixed="true" type="string"/>
<item alias="农民工门大病种" id="NMGMDBZ" length="400" fixed="true" type="string"/>
<item alias="农民工特药资格" id="NMGTYZG" length="3" fixed="true" type="string"/>
<item alias="农民工特药病种" id="NMGTYBZ" length="400" fixed="true" type="string"/>
<item alias="农民工特药名称编码" id="NMGTYMCBM" length="400" fixed="true" type="string"/>
<item alias="能否享受职工门诊统筹" id="NFXSZGMZTC" length="3" fixed="true" type="string"/>
<item alias="生育审批类型" id="SYSPLX" length="3" fixed="true" type="string">
	<dic>
		<item key="25"  text="居民生育"/>
		<item key="28"  text="计生手术"/>
		<item key="29"  text="妊娠前期"/>
		<item key="30"  text="妊娠后期"/>
		<item key="31"  text="分娩"/>
	</dic>
</item>
<item alias="封锁原因" id="FSYY" length="3" fixed="true" type="string">
	<dic>
		<item key="11"  text="单位全封锁"/>
		<item key="12"  text="单位封统筹"/>
		<item key="13"  text="个人全封锁"/>
		<item key="14"  text="个人封统筹"/>
	</dic>
</item>	
<item alias="门慢剩余可报金额" id="MMSYKBJE" length="16" fixed="true" type="string"/>
<item alias="门特辅助治疗剩余可报金额" id="MTFZZLSYKBJE" length="16" fixed="true" type="string"/>
<item alias="工伤待遇资格" id="GSDYZG" length="3" fixed="true" type="string">
	<dic>
		<item key="1"  text="享受"/>
		<item key="0"  text="不享受"/>
	</dic>
</item>
<item alias="工伤待遇病种" id="GSDYBZ" length="400" fixed="true" type="string"/>
<item alias="工伤诊断结论" id="GSZDJL" length="400" fixed="true" type="string"/>
<item alias="大卡剩余可报金额" id="DKSYKBJE" length="16" fixed="true" type="string"/>
<item alias="门统剩余可报金额" id="MTSYKBJE" length="16" fixed="true" type="string"/>
<item alias="医保家床资格" id="YBJCZG" length="3" fixed="true" type="string">
	<dic>
		<item key="1"  text="享受"/>
		<item key="0"  text="不享受"/>
	</dic>
</item>
<item alias="医保门诊专项资格" id="YBMZZXZG" length="3" fixed="true" type="string">
	<dic render="LovCombo">
		<item key="1"  text="享受"/>
		<item key="0"  text="不享受"/>
	</dic>
</item>
<item alias="医保门诊专项药品通用名编码" id="YBMZZXYPTYMBM" length="400" fixed="true" type="string"/>
<item alias="困难建档立卡人员" id="SFKNJDLK" length="3" fixed="true" type="string">
	<dic render="LovCombo">
		<item key="1"  text="是困难建档立卡人员"/>
		<item key="0"  text="不是困难建档立卡人员"/>
	</dic>
</item>
</entry>
