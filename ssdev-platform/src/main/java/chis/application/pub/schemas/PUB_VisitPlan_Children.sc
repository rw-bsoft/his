<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pub.schemas.PUB_VisitPlan" alias="随访计划" sort="beginDate">
  <item id="planId" pkey="true" alias="计划识别" type="string" length="16"
    not-null="1" fixed="true" hidden="true"/>
  <item id="sn" alias="序号" type="int" display="0"/>
  <item id="beginDate" alias="开始日期" type="date"  hidden="true" />
  <item id="endDate" alias="结束日期" type="date"  hidden="true" />
  <item id="beginVisitDate" alias="提醒日期" type="date"  hidden="true" />
  <item id="planStatus" alias="计划状态" type="string" length="1" default="0" hidden="true">
    <dic>
      <item key="0" text="应访"/>
      <item key="1" text="已访"/>
      <item key="2" text="失访" />
      <item key="3" text="未访"/>
      <item key="4" text="过访"/>
      <item key="8" text="结案"/>
      <item key="9" text="注销"/>
    </dic>
  </item>
  <item id="visitId" alias="随访记录ID" type="string" length="16" hidden="true"/>
  <item id="extend1" alias="月龄" type="int" length="60" width="60" display="1">
    <dic id="childrenAge" />
  </item>
  <item id="planDate" alias="计划日期" type="date" width="80"/>
  <item id="visitDate" alias="处理日期" type="date" width="80"/>
  <item id="businessType" alias="计划类型" type="string" length="1" hidden="true" >
    <dic>
      <item key="1" text="高血压" />
      <item key="2" text="糖尿病" />
      <item key="3" text="肿瘤" />
      <item key="4" text="老年人保健" />
      <item key="5" text="儿保" />
      <item key="6" text="妇保" />
    </dic>
  </item>
  <item id="groupCode" alias="分组组别" type="string" length="2"
    hidden="true" >
    <dic>
      <item key="01" text="一组" />
      <item key="02" text="二组" />
      <item key="03" text="三组" />
      <item key="99" text="一般管理" />
    </dic>
  </item>
  <item id="fixGroupDate" alias="转组日期" type="date" hidden="true" />
  <item id="recordId" alias="档案编号" type="string" length="30" hidden="true" />
  <item id="empiId" alias="EMPIID" type="string" length="32" hidden="true" />
  <item id="remark" alias="备注" type="string" length="500" display="0"/>
	
  <item id="extend2" alias="扩展字段2" type="string" length="60" display="0"/>
</entry>
