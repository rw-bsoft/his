<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 			
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['like', ['$','a.sourceManaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.targetManaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.applyUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]]
				</filter>			
			</conditions>
		</p>
		<p principal="T08" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['like', ['$','a.sourceManaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.targetManaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.applyUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['like', ['$','a.sourceManaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.targetManaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.applyUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>