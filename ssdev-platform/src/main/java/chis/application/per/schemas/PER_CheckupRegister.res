<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 			
			</conditions>
		</p>
		<p principal="T01" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T06" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T07" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T08" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T13" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.inputUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>