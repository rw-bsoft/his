<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BASE_Role" alias="主键生成测试">
    <item id="id" alias="id" type="string" pkey="true" length="50" display="1" width="150">
       <key>
         <rule name="increaseId" type="increase" defaultFill="0" length="5" startPos="0" seedRel="area"/>        
         <rule name="area" type="string" defaultFill="0" length="10" fillPos="after">
            %user.manageUnit.id
         </rule>
         
       </key>
    </item>
	<item id="name" alias="名称" type="string"/>
	<item id="description" alias="描述" type="string" />  
</entry>