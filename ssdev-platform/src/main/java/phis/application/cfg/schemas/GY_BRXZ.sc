<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_BRXZ" alias="病人性质">
  <item id="BRXZ" alias="性质代码" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" type="increase" length="12" startPos="31"/>
    </key>
  </item>
  <item id="PLSX" alias="顺序号" type="string" length="10" not-null="1"/>
  <item id="XZMC" alias="性质名称" type="string" not-null="1" length="30"/>
  <item id="SJXZ" alias="上级性质" length="18" type="long" display="0" not-null="1"/>
  <item id="GSXZ" alias="归属性质" length="18" type="long" display="0" not-null="1"/>
  <item id="PYDM" alias="拼音代码" type="string" length="6" target="XZMC" codeType="py" />
  <item id="DBPB" alias="险种" type="int" length="1" not-null="1" defaultValue="0">
  	<dic>
  		<item text="非保险" key="0"/>
  		<item text="大病保险" key="1"/>
  		<item text="医疗保险" key="2"/>
  		<item text="其他保险" key="9"/>
  	</dic>
  </item>
  <item id="XZDL" alias="性质控制大类"  length="10" >
  	<dic>
  		<item text="医保" key="1"/>
  		<item text="离休" key="2"/>
  		<item text="其他" key="3"/>
  	</dic>
  </item>
  <!--性质控制小类,暂时定为 1是市医保  其他类别自行添加, 医保判断为XZDL为1 才会生效-->
   <item id="XZXL" alias="性质控制小类"  length="2" type="int" defaultValue="1">
  	<dic>
  		<item text="市医保" key="1"/>
  	</dic>
  </item>
  <item id="ZHPB" alias="证号判别"  length="1" not-null="1" defaultValue="0">
  	<dic>
  		<item text="不需要" key="0"/>
  		<item text="需要" key="1"/>
  	</dic>
  </item>
  <item id="MZSY" alias="门诊使用" length="1" type="int" not-null="1" defaultValue="1">
  	<dic id="phis.dictionary.confirm" />
  </item>
  <item id="ZYSY" alias="住院使用" length="1" type="int" not-null="1" defaultValue="1">
  	<dic id="phis.dictionary.confirm" />
  </item>
  <item id="SSBL" alias="实收比例" type="double" length="4" display="0" not-null="1" defaultValue="1"/>
  <item id="CFXJ" alias="床费限价" type="double" length="13" minValue="0" maxValue="9999999999.99" not-null="1" defaultValue="0"/>
  <item id="CFXE" alias="处方限额" type="double" length="13" not-null="1" minValue="0" maxValue="9999999999.99" defaultValue="0"/>
  <item id="TFFBJY" alias="退费分步交易" length="1" type="int" not-null="1" defaultValue="0">
  	<dic id="phis.dictionary.confirm" />
  </item>
</entry>
