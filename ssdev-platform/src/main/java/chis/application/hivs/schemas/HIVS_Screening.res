<entity>
  <permissions>
    <p principal="$Others" mode="15">
      <conditions>
        <filter action="query">
			['like', ['$','a.createUnit'], ['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]		 
		</filter>                
      </conditions>
    </p>
  </permissions>
</entity>