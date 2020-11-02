<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mzf.schemas.MZF_VisitRecord" alias="慢阻肺随访记录">	
	<item id="visitId" pkey="true" alias="记录序号" type="string" display="0" width="160" length="16"  hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30"  width="160" fixed="true"  queryable="true"/> 
	<item id="empiId" alias="EMPIID" type="string" length="32"  fixed="true" display="2" />	
	<item ref="b.personName" alias="姓名" type="string" length="20"  display="1" queryable="true"/>	
	<item ref="b.sexCode" alias="性别" type="string" length="1" display="1" queryable="false">	
		<dic id="chis.dictionary.gender"/>
	</item>   
	<item ref="b.birthday" alias="出生日期" type="date" display="1" maxValue="%server.date.today" queryable="false"/>      
	<item ref="b.idCard" alias="身份证号码" type="string" display="1" width="150" />
	<item ref="c.checkType" type="string" display="1" virtual="true"  alias="是否年检" />
	<item ref="b.mobileNumber" alias="本人电话" display="1" type="string" length="20" />	
	<item id="SFRQ" alias="随访日期" not-null="true" type="date" defaultValue="%server.date.today" enableKeyEvents="true" validationEvent="false" />
	<item id="SFFS" alias="随访方式" type="string" length="1" not-null="true">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item id="ZZ" alias="症状" xtype="checkbox" display="2" type="string" length="100" width="300">
		<dic render="Checkbox">
			<item key="1" text="无症状" />
			<item key="2" text="咳嗽" />
			<item key="3" text="咳痰" />
			<item key="4" text="气急" />
			<item key="5" text="喘憋" />
			<item key="6" text="呼吸困难" />
			<item key="7" text="咯血" />
			<item key="8" text="乏力" />
		</dic>
	</item>
	<item id="QTZZ" alias="症状(其他)" display="2" type="string" length="50"/>
	<item id="SG" alias="身高(cm)" colspan="2" display="2" type="string" length="3"/>
	<item id="DQTZ" alias="当前体重(kg)" display="2" type="string" length="3" />
	<item id="MBTZ" alias="目标体重(kg)" display="2" type="string" length="3" />
	<item id="SSY" alias="收缩压(mmHg)" display="2"  type="string" minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="SZY" alias="舒张压(mmHg)" display="2" type="string" minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="XL" alias="心率(次/分)" display="2"  type="string" />
	<item id="HXPL" alias="呼吸频率(次/分)" display="2"  type="string" />
	<item id="QTTZ" alias="其他体征" display="2" type="string" length="100">
		<dic render="Checkbox">
			<item key="1" text="桶状胸" />
			<item key="2" text="双下肢浮肿" />
			<item key="3" text="杵状指(趾)" />
			<item key="4" text="口唇紫绀" />
			<item key="5" text="甲床紫绀" />
			<item key="6" text="呼吸音减弱" />
			<item key="7" text="肺部啰音" />
			<item key="8" text="颈静脉怒张" />
		</dic>
	</item>
	<item id="QTQTTZ" alias="其他体征(其他)" display="2" type="string" length="50"/>
	<item id="NNJXJZCS" alias="年内急性加重次数" display="2" type="string" length="5" />
	<item id="NNYJXJZZYCS" alias="年内因急性加重住院次数" display="2" type="string" length="5" />
	<item id="DQRXYL" alias="当前日吸烟量" display="2" type="string" length="5" />
	<item id="MBRXYL" alias="目标日吸烟量" display="2" type="string" length="5" />
	<item id="DQYDZ" alias="当前运动(次/周)" display="2" type="string" length="5" />
	<item id="DQYDC" alias="当前运动(分钟/次)" display="2" type="string" length="5" />
	<item id="MBYDZ" alias="目标运动(次/周)" display="2" type="string" length="5" />
	<item id="MBYDC" alias="目标运动(分钟/次)" display="2" type="string" length="5" />
	<item id="XLTZ" colspan="2" alias="心理调整" display="2" type="string">
		<dic render="Radio">
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="ZYXW" alias="遵医行为" display="2" type="string">
		<dic render="Radio">
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>	
	<item id="FEV1" alias="FEV1(L)" display="2" type="string" length="5" />
	<item id="FEV1ANDFVC" alias="FEV1/FVC(%)" display="2" type="string" length="5" />
	<item id="FEV1ANDYJZ" alias="FEV1/预计值(%)" display="2" type="string" length="5" />
	<item id="SP02" alias="Sp02(%)" display="2" type="string" length="5" />
	<item id="XHDB" alias="血红蛋白(g/L)" display="2" type="string" length="5" />
	<item id="HXB" alias="红细胞(×109/L)" display="2" type="string" length="5" />
	<item id="BXB" alias="白细胞(×109/L)" display="2" type="string" length="5" />
	<item id="XXB" alias="血小板(×109/L)" display="2" type="string" length="5" />
	<item id="LBXB" alias="淋巴细胞(×109/L)" display="2" type="string" length="5" />
	<item id="ZXLXB" alias="中性粒细胞(%)" display="2" type="string" length="5" />
	<item id="SSXXB" alias="嗜酸性细胞(%)" display="2" type="string" length="5" />
	<item id="XXXPBG" colspan="2" alias="X线胸片报告" xtype="textarea" display="2" type="string" length="100" />
	<item id="XDTBG" colspan="2" alias="心电图报告" xtype="textarea" display="2" type="string" length="100" />
	<item id="CATPGDF" alias="CAT评估得分" display="2" type="string" length="5" />
	<item id="MMRCDF" alias="mMRC得分" display="2" type="string" length="5" />
	<item id="QLSXGOLDFJ" alias="气流受限GOLD分级" display="2" type="string" length="5" />
	<item id="GOLDZHPGFZ" alias="COPD综合评估分组" display="2" type="string" length="5" />
	<item id="MZFGLDJ" colspan="2" alias="慢阻肺管理等级" display="2" type="string" length="100">
		<dic render="Radio">
			<item key="1" text="稳定期低风险" />
			<item key="2" text="稳定期高风险" />
			<item key="3" text="急性加重期" />
		</dic>
	</item>	
	<item id="FYYCX" colspan="2" alias="用药依从性" display="2" type="string" length="100">
		<dic render="Radio">
			<item key="1" text="规律" />
			<item key="2" text="间断" />
			<item key="3" text="不用药" />
		</dic>
	</item>
	<item id="YWBLFY" colspan="2" alias="药物不良反应" display="2" type="string" length="100">
		<dic render="Radio">
			<item key="1" text="无" />
			<item key="2" text="轻微" />
			<item key="3" text="严重" />
		</dic>
	</item>
	<item id="XRYWZZSY" colspan="2" alias="吸入药物装置使用" display="2" type="string" length="100">
		<dic render="Radio">
			<item key="1" text="熟练" />
			<item key="2" text="不熟练" />
			<item key="3" text="未掌握" />
		</dic>
	</item>	
	<item id="XJZZZD" alias="吸入装置指导" display="2" type="string" length="100" />
	<item id="FKFZL" alias="肺康复指导" display="2" type="string" length="100" />
	<item id="YL" alias="氧疗" display="2" type="string" length="100" />
	<item id="WCHXJZL" alias="无创呼吸机治疗" display="2" type="string" length="100" />
	<item id="YMJZ" colspan="2" alias="疫苗接种" display="2" type="string" length="100">
		<dic render="Checkbox">
			<item key="1" text="流感疫苗" />
			<item key="2" text="肺炎疫苗" />
		</dic>
	</item>
	<item id="YWMC1" alias="药物名称1" display="2" type="string" length="100" />
	<item id="YFYL1" alias="用法用量(次/日)" display="2" type="string" length="100" />
	<item id="YWMC2" alias="药物名称2" display="2" type="string" length="100" />
	<item id="YFYL2" alias="用法用量(次/日)" display="2" type="string" length="100" />
	<item id="YWMC3" alias="药物名称3" display="2" type="string" length="100" />
	<item id="YFYL3" alias="用法用量(次/日)" display="2" type="string" length="100" />
	<item id="YY" alias="原因" display="2" type="string" length="100" />
	<item id="JGJKB" alias="机构及科别" display="2" type="string" length="100" />
	<item id="XCSFRQ" alias="下次随访日期"  not-null="true" type="date" enableKeyEvents="true" validationEvent="false" queryable="true"/>
	<item id="SFYSQM" alias="随访医生"  type="string" length="100">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" 
		display="3" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" 
		display="3" defaultValue="%user.manageUnit.id" width="250" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield"  
		display="3" defaultValue="%server.date.date" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		hidden="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
	<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1" hidden="true">
	    <dic>
	      <item key="1" text="死亡" />
	      <item key="2" text="迁出" />
	      <item key="3" text="失访" />
	      <item key="4" text="拒绝" />
	      <item key="6" text="作废" />
	      <item key="9" text="其他" />
	    </dic>
  </item>
	<relations>
	    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
	      	<join parent="empiId" child="empiId" />
	    </relation>
	    <relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord">
	  		<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>

