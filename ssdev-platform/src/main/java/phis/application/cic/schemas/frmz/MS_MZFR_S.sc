<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZFR_S" tableName="MS_MZFR"  alias="发热门诊">
    <item id="ZDXH" alias="诊断名称" length="4" type="string" not-null="1">
        <dic render="LovCombo">
            <item key="J12.800+B97.200" text="确诊新型冠状病毒肺炎"/>
            <item key="J12.800+B97.200+Z11.500" text="疑似新型冠状病毒肺炎"/>
            <item key="B34.2" text="核酸检测阳性，而无肺炎表现"/>
            <item key="Z20.800" text="接触和暴露于新型冠状病毒"/>
            <item key="Z11.500" text="新型冠状病感染的特殊筛查"/>
            <item key="098.800" text="妊娠合并新型冠状病毒肺炎"/>
        </dic>
	</item>
	<item id="XCGJG" alias="血常规结果" type="string" length="10"  />
	<item id="CPR" alias="高敏感CPR" length="4"    type="int">
	    <dic>
            <item key="1" text="阴性"/>
            <item key="2" text="阳性"/>
        </dic>
	</item>
	<item id="YSZ" alias="咽试因子" type="int" length="40"  >
		<dic>
             <item key="1" text="阴性"/>
             <item key="2" text="阳性"/>
        </dic>
    </item>
	<item id="SFCT" alias="双肺CT" type="int" length="16"  >
        <dic>
            <item key="1" text="正常"/>
            <item key="2" text="其他非新冠影像"/>
            <item key="3" text="多发小班片影及间质改变"/>
            <item key="4" text="双肺多发磨玻璃影、浸润影"/>
            <item key="5" text="肺实变"/>
            <item key="6" text="胸腔积液"/>
        </dic>
	</item>
	<item id="RTPCR" alias="核酸检测" type="int" length="10"  >
	    <dic>
            <item key="1" text="阴性"/>
            <item key="2" text="阳性"/>
        </dic>
	</item>
	<item id="LRRY" alias="录入人" length="10"  type="string" defaultValue="%user.userId" readOnly="true">
        <dic id="phis.dictionary.doctor" />
    </item>
	<item id="JGID" alias="录入机构" type="string" length="30" width="150" display="0"/>
	<item id="LRSJ" alias="录入时间"  not-null="1"  type="datetime"  xtype="datefield"  defaultValue="%server.date.datetime"/>
	<item id="XGR" alias="修改人" length="10"    type="string" display="0"/>
	<item id="XGSJ" alias="修改时间"  width="150"  display="0" xtype="datetimefield" type="datetime" defaultValue="%server.date.datetime"/>

</entry>
