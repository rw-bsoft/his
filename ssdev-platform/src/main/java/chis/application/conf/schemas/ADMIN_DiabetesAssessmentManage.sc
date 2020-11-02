<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_DiabetesAssessmentManage" alias="糖尿病评估标准维护" >
	<item id="assessType" alias="评估方式" type="string">
		<dic>
			<item key="1" text="随访评估(单个病人在随访完成后，系统自动执行)"/>
			<item key="2" text="年度评估(人工启动，所有病人一起评估)"/>
		</dic>
	</item>
	<item id="assessYearCon" alias="年度评估条件" type="string">
		<dic>
			<item key="1" text="排除糖尿病档案已注销或者是个人档案已注销的病人"/>
			<item key="2" text="新病人不评价(维持原组不变)"/>
			<item key="3" text="未规范管理的病人不进行年度评估"/>
		</dic>
	</item>
	<item id="normManage" alias="各分组随访次数最低标准" type="string">
		<dic>
			<item key="1" text="一组随访次数最低标准"/>
			<item key="2" text="二组随访次数最低标准"/>
			<item key="3" text="三组随访次数最低标准"/>
		</dic>
	</item> 
	<item id="normScale1" alias="一组随访比例"  type="double" length="6" precision="2"/>
	<item id="normScale2" alias="二组随访比例"  type="double" length="6" precision="2"/>
	<item id="normScale3" alias="三组随访比例"  type="double" length="6" precision="2"/>
	<item id="fineScore11" alias="空腹血糖优良下限值" type="double" length="6" precision="2"/>
	<item id="fineScore12" alias="空腹血糖优良上限值" type="double" length="6" precision="2"/>
	<item id="fairScore1" alias="空腹血糖尚可值" type="double" length="6" precision="2"/>
	<item id="badScore1" alias="空腹血糖不良值" type="double" length="6" precision="2"/>
	<item id="fineScore21" alias="餐后血糖优良下限值" type="double" length="6" precision="2"/>
	<item id="fineScore22" alias="餐后血糖优良上限值" type="double" length="6" precision="2"/>
	<item id="fairScore2" alias="餐后血糖尚可值" type="double" length="6" precision="2"/>
	<item id="badScore2" alias="餐后血糖不良值" type="double" length="6" precision="2"/>
	<item id="scaleNum1" alias="控制优良次数比例" type="double" length="6" precision="2"/>
	<item id="scaleNum2" alias="控制不良次数比例" type="double" length="6" precision="2"/>
	<item id="threeTurn" alias="三组转组标准"  type="string">
		<dic>
			<item key="1" text="本年度高危筛查有上级诊断医院，转到二组"/>
			<item key="2" text="按血糖值转到一组或二组"/>
		</dic>
	</item>
	<item id="assessDays" alias="距年末的最大可评估天数" type="int" length="1" maxValue="5"/>
	<item id="assessHour1" alias="可评估时间" type="int" length="2"  maxValue="24"/>
	<item id="assessHour2" alias="可评估时间" type="int" length="2"  maxValue="24"/>
</entry>
