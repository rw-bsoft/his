<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_ZY02" alias="医技项目取消住院项目集合" >
	<item ref="b.FYMC" alias="费用名称"  length="80" type="string" not-null="1" />
	<item ref="b.FYDW" alias="费用单位"  length="4" type="string"  />
	<item id="YLDJ" alias="医疗单价"  length="10" type="double" precision="2"  not-null="1"/>
	<item id="YLSL" alias="医疗数量"  length="8" type="double" precision="2" not-null="1"/>
	<!--TPLJ在数据库中为医技图片字段，此处暂时用于存放医疗单价与医疗数量量的乘积 -->
	<item id="TPLJ" alias="金额" length="50" type="string" precision="2" defaultValue="0" renderer="showJE" />
	<item id="ZFBL" alias="自负比例"  length="5" type="double" precision="3" not-null="1" />
	<item id="YZXH" alias="医嘱序号"  length="18" type="long" not-null="1" display="0"/>
	<item id="YEPB" alias="婴儿唯一号"  length="1" type="long" display="0"/>
	
	<!-- 
	<item id="FYGB" alias="费用归并"  length="4" type="long" not-null="1" />
	-->
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_YLSF" >
			<join parent="FYXH" child="YLXH"></join>
		</relation>
	</relations>
</entry>