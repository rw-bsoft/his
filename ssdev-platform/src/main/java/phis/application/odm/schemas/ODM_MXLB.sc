<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ODM_MXLB" alias="自备药明细列表" >
		<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0"  pkey="true" />
		<item id="KDSJ" alias="开单时间" type="datetime" width="240"/>
		<item id="HM" alias="处方号码" type="string" />
		<item id="BRXM" alias="病人姓名" type="string" />
		<item id="YPMC" alias="药品名称" type="string" width="180"  />
		<item id="YPGG" alias="规格" type="string" width="60"  />
		<item id="YPJL" alias="剂量" type="string" width="60"  />
		<item id="YPYF" alias="频次" type="string" not-null="true" length="18" width="60" />
		<item id="YYTS" alias="天数" type="int"  />
		<item id="YPSL" alias="总量" type="int"  />
		<item id="YFDW" alias="单位" type="string"  />
		<item id="TZSJ" alias="停嘱时间" type="datetime" width="180"/>
</entry>
