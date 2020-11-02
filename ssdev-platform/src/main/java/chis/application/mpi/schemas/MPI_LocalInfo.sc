<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mpi.schemas.EMPIPersonalInfo" alias="EMPI个人基本信息">
	<item id="localInfoId"    	     				alias="编号"   			type="string" 	length="16"   not-null="1"  pkey="true"  queryable="true"/>
   <item id="empiId"        alias="EMPI"       type="string" length="32"/>
   <item id="sourceId"      alias="机构"       type="string" length="16"/>
   <item id="localId"       alias="LOCALID"       type="string" length="32"/>
   <item id="createTime"    alias="创建时间"       type="date"/>
   <item id="modifyTime"    alias="修改时间"       type="date"/>
</entry>