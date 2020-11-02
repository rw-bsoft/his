<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.dataset.DATASET" name="资源目录">
	<catagory id="DATASET_CATA" name="资源目录">
		<module id="DATASET_01" name="标准管理" script="platform.dataset.client.DataMain">
			<action id="dataelement" name="数据元" ref="platform.dataset.DATASET/DATASET_CATA/DATASET_01_01"/>
			<action id="dataeset" name="数据集" ref="platform.dataset.DATASET/DATASET_CATA/DATASET_01_02"/>
			<action id="datadic" name="数据字典" ref="platform.dataset.DATASET/DATASET_CATA/DATASET_01_03"/>
		</module>
		<module id="DATASET_01_01" name="数据元" script="platform.dataset.client.DataElementView" type="1">
			<action id="add" name="新增" />
			<action id="update" name="修改"/>
			<action id="remove" name="删除"/>
			<action id="addDeCata" name="新增数据元分类" iconCls="add" handler="this.doAddDeCata"/>
			<action id="updateDeCata" name="修改数据元分类" iconCls="update" handler="this.doAddDeCata"/>
			<action id="deleteDeCata" name="删除数据元分类" iconCls="remove" handler="this.doDeleteDeCata"/>
		</module>
		<module id="DATASET_01_02" name="数据集" script="platform.dataset.client.DataSetView" type="1">
			<action id="addGroup" name="添加组" iconCls="common_treat"/>
			<action id="add" name="添加元"/>
			<action id="sequence" name="保存顺序" iconCls="common_treat"/>
			<action id="update" name="修改"/>
			<action id="remove" name="删除"/>
			<action id="addSetOrCatalog" name="新增目录或数据集" iconCls="add"/>
			<action id="updateSetOrCatalog" name="修改目录或数据集" iconCls="update" handler="this.doUpdateCatalogOrSet"/>
			<action id="deleteSetOrCatalog" name="删除目录或数据集" iconCls="remove" handler="this.doRemoveCatalogOrSet"/>
		</module>
		<module id="DATASET_01_03" name="数据字典" script="platform.dataset.client.DataDicView" type="1">
		</module>
		<module id="DATASET_02" name="标准发布" script="platform.dataset.client.DataSetSelectListView">
			<properties>
				<p name="entryName">platform.dataset.schemas.RES_DataSet</p>
			</properties>
			<action id="read" name="查看" />
			<action id="zip" name="下载选中发布信息" iconCls="app" />
			<action id="zipList" name="下载列表中所有信息" iconCls="app" />
		</module>
		<module id="DATASET_03" name="字典审核"	script="platform.dataset.client.DataDicVerifyView">
			<properties>
				<p name="dicId">dictionaries.platform.dataset.dic.verify</p>
			</properties>
			<action id="verify" name="审核" iconCls="add"/>
			<action id="reject" name="拒绝" iconCls="remove"/>
		</module>
		<module id="DATASET_04" name="字典审核记录" script="app.modules.list.SimpleListView">
			<properties>
				<p name="entryName">platform.dataset.schemas.RES_DictionaryVerifyLog</p>
			</properties>
		</module>
	</catagory>
</application>