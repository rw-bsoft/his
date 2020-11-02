<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_LNJBZL" alias="老年基本资料" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="shsy" alias="生活赡养" type="string" colspan="4">
		<dic render="LovCombo">
			<item key="1" text="本人"/>
			<item key="2" text="配偶"/>
			<item key="3" text="子女"/>
			<item key="4" text="亲戚"/>
			<item key="5" text="政府"/>
			<item key="6" text="集体"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	<item id="pf1" alias="1)使用公共汽车" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf2" alias="2)行走" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf3" alias="3)做饭菜" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf4" alias="4)做家务" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf5" alias="5)吃药" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf6" alias="6)吃饭" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf7" alias="7)穿衣" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf8" alias="8)梳头、刷牙等" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf9" alias="9)洗衣" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf10" alias="10)洗澡" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf11" alias="11)购物" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf12" alias="12)定时上厕所" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf13" alias="13)打电话" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="pf14" alias="14)处理自己财务" type="string" colspan="2" group="评价表">
		<dic render="Radio" colWidth="50">
			<item key="1" text="1"/>
			<item key="2" text="2"/>
			<item key="3" text="3"/>
			<item key="4" text="4"/>
		</dic>
	</item>
	<item id="info" alias="各项目评分标准" type="string" defaultValue="1、自己完全可以做 2、有些困难 3、需要帮助 4、根本无法做 
	结果的评价方法：总分&lt;=16分为完全正常，&gt;16分有不同程度的功能下降，凡有2项或2项以上&gt;=3
	或总分&gt;=22为功能有明显障碍" fixed="true" xtype="displayfield" height="30" colspan="4" group="评价表"/>
	
	<item id="pjjg" alias="评价结果" type="string" fixed="true" colspan="2" group="评价表"/>
	<item id="hlqk" alias="护理情况" type="string" colspan="2">
		<dic render="LovCombo">
			<item key="1" text="不需"/>
			<item key="2" text="配偶"/>
			<item key="3" text="子女"/>
			<item key="4" text="亲友"/>
			<item key="5" text="保姆"/>
			<item key="6" text="养老机构"/>
			<item key="7" text="邻居"/>
			<item key="8" text="其他社会福利机构"/>
		</dic>
	</item>
	<item id="slzs" alias="视力指数" type="string" colspan="2">
		<dic>
			<item key="1" text="裸眼可辨"/>
			<item key="2" text="裸眼不可辨"/>
			<item key="3" text="戴镜可辨"/>
			<item key="4" text="戴镜不可辨"/>
			<item key="5" text="老光"/>
			<item key="6" text="白内障"/>
		</dic>
	</item>
	<item id="yccq" alias="牙齿残缺" type="string" colspan="2">
		<dic>
			<item key="1" text="不缺"/>
			<item key="2" text="部分缺"/>
			<item key="3" text="全缺"/>
		</dic>
	</item>
	
	<item id="bdcz" alias="被调查者" type="string" defaultValue="" group="调查"/>
	<item id="dcz" alias="调查者" type="string" defaultValue="" group="调查">
	</item>
	<item id="jlz" alias="记录者" type="string" defaultValue="" fixed="true" group="调查">
	</item>
	<item id="qmrq" alias="签名日期" type="datetime" xtype="datefield" group="调查"/>
	<item id="dcrq" alias="调查日期" type="datetime" xtype="datefield" group="调查"/>
	<item id="jlrq" alias="记录日期" type="datetime" xtype="datefield" fixed="true" group="调查"/>
</entry>
