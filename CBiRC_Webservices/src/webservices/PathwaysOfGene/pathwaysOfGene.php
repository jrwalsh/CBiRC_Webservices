<html>
<head>
	<title>Gene Pathway Converter</title>
</head>
<body>

<?php
if (isset($_POST['ids'])) {
	translate($_POST['ids'],($_POST['selectOrganism']),$_POST['selectType']);
} else {
	print_form();
}

function translate($ListOfIDs,$Organism,$Type)
{
$cmd = ("java -jar GenesPathways.jar $ListOfIDs $Type $Organism");
        exec($cmd, $output, $ret);
        foreach ($output as $value) {
                echo $value;
                echo "<br />";
        }
        if (count($output) == 0) {
        	echo "Failed to run query with given parameters";
        }
}

function print_form() {
	echo '
	<form action="pathwaysOfGene.php" method="post">
	
		<p>Organism<br>
		<select name="selectOrganism" id="selectOrganism">
		<option value="ECOLI" selected>E. coli</option>
		<option value="META">MetaCyc</option>
		<option value="ARA">Arabidopsis</option>
		</select>
		</p>
		
		<p>Type of ID supplied<br>
		<select name="selectType" id="selectType">
		<option value="0" selected>Pathway IDs</option>
		<option value="1">Gene IDs</option>
		</select>
		</p>
		
		Genes: <input type="text" name="ids" id="ids" />
	
	<input name="submit" type="submit" value="submit" />
	</form>
	
	<p><bold>Instructions:</bold></p>
	<p>
	Any single gene or pathway id can be put into the text box.  You can select an organism and the type of ID you are supplying.</p>
	<p>
	Multiple id inputs can be used as well.  They must be comma separated with no spacing, and can be placed in the text box the same as a single id.
	They must all be either gene or pathway ids, but you cannot mix the gene IDs and pathway IDs in the same query.</p>
	';
}
?>

</body>
</html>