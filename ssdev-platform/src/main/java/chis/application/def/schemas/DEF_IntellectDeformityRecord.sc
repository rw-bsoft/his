<?xml version="1.0" encoding="UTF-8"?>
<entry alias="残疾人训练登记">
  
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" alias="家庭住址"/>
	<item ref="c.regionCode_text" display="0"/>
	<!--
		<item ref="d.updateScore" display="1" queryable="true" />
		<item ref="d.trainEffect" display="1" queryable="true" />
		<item ref="d.healingTarget" display="1" queryable="true" />
		<item ref="d.recoverSuggestion" display="1" queryable="true" />
		-->
	<item id="phrId" alias="健康档案号" length="30" display="0"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="homeAddress" alias="家庭住址" type="string" virtual="true" colspan="2" fixed="true" display="2"/>
	<item id="contactPhone" alias="联系电话" length="20" />
	<item id="parentName" alias="家长姓名" length="20" />
	<item id="relation" alias="与残疾儿童关系" length="20" >
		<dic>
			<item key="51" text="父亲" />
			<item key="52" text="母亲" />
			<item key="57" text="继父或养父" />
			<item key="58" text="继母或养母" />
			<item key="59" text="其他父母关系" />
			<item key="61" text="祖父" />
			<item key="62" text="祖母" />
			<item key="63" text="外祖父" />
			<item key="64" text="外祖母" />
			<item key="65" text="配偶的祖父母或外祖父母" />
			<item key="66" text="曾祖父" />
			<item key="67" text="曾祖母" />
			<item key="68" text="配偶的曾祖父母或外曾祖父母" />
			<item key="69" text="其他祖父母或外祖父母关系" />
			<item key="71" text="兄" />
			<item key="72" text="嫂" />
			<item key="73" text="弟" />
			<item key="74" text="弟媳" />
			<item key="75" text="姐姐" />
			<item key="76" text="姐夫" />
			<item key="77" text="妹妹" />
			<item key="78" text="妹夫" />
			<item key="79" text="其他兄弟姐妹" />
			<item key="81" text="伯父" />
			<item key="82" text="伯母" />
			<item key="83" text="叔父" />
			<item key="84" text="婶母" />
			<item key="85" text="舅父" />
			<item key="86" text="舅母" />
			<item key="87" text="姨父" />
			<item key="88" text="姨母" />
			<item key="89" text="姑父" />
			<item key="90" text="姑母" />
			<item key="91" text="堂兄弟、堂姐妹" />
			<item key="92" text="表兄弟、表姐妹" />
			<item key="93" text="侄子" />
			<item key="94" text="侄女" />
			<item key="95" text="外甥" />
			<item key="96" text="外甥女" />
			<item key="97" text="其他亲属" />
			<item key="99" text="非亲属" />
		</dic>

	</item>
	<item id="withOtherDeformity" alias="是否伴有其他残疾" length="20">
		<dic render="LovCombo">
			<item key="1" text="视力" />
			<item key="2" text="智力" />
			<item key="3" text="听力" />
			<item key="4" text="言语" />
			<item key="5" text="癫痫" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="deformityDate" alias="确诊时间" type="date" not-null="1" maxValue="%server.date.today"/>
	<item id="deformityReason" alias="残疾原因" length="100"  not-null="1">
		<dic render="LovCombo">
			<item key="1" text="先天" />
			<item key="2" text="早产" />
			<item key="3" text="难产" />
			<item key="4" text="疾病" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="diagnosticUnit" alias="诊断机构" length="16"/>
	<item id="iq" alias="智商" type="int" length="4" maxValue="1000"/>
	<item id="action" alias="社会适应行为" length="100"/>
	<item id="pastMedicalTreatment" alias="既往医疗情况" length="100" not-null="1">
		<dic render="LovCombo">
			<item key="1" text="药物治疗" />
			<item key="2" text="传统方法" />
			<item key="3" text="康复训练" />
			<item key="4" text="上幼儿园" />
			<item key="5" text="上学" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		queryable="true" not-null="1" update="false" defaultValue="$user.userId">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		colspan="2" anchor="100%" width="180" not-null="1" fixed="true"  queryable="true"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree"
			parentKey="%user.manageUnit.id" rootVisible="false" />
	</item>
	<item id="explanation" alias="需要说明的情况" length="500" xtype="textarea" colspan="3" width="200"/>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="closeFlag" alias="结案标识" type="string" length="1"
		display="0" defaultValue="n" queryable="true">
		<dic>
			<item key="n" text="未结案" />
			<item key="y" text="已结案" />
		</dic>
	</item>
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1"
		queryable="true" display="0">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="6" text="作废" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20" fixed="true" colspan="2" anchor="100%" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" maxValue="%server.date.today" fixed="true" display="0">
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		fixed="true" colspan="2" anchor="100%" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="createUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<!--
			<relation type="children" entryName="DEF_IntellectTerminalEvaluate">
				<join parent="id" child="defId" />
			</relation>
			-->
	</relations>
</entry>
