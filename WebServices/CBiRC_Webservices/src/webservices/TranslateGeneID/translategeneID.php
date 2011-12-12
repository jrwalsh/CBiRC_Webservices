<html>
<head>
	<title>Gene ID Translator</title>
</head>
<body>

<?php
if (isset($_POST['geneIDs'])) {
	translate($_POST['geneIDs'],($_POST['selectOrganism']),$_POST['selectType']);
} else {
	print_form();
}

function translate($ListOfGeneIDs,$Organism,$Type)
{
$cmd = ("java -jar GeneIDTranslate.jar $ListOfGeneIDs $Type $Organism");
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
	<form action="translategeneID.php" method="post">
	
		<p>Organism<br>
		<select name="selectOrganism" id="selectOrganism">
		<option value="ECOLI" selected>E. coli</option>
		<option value="META">MetaCyc</option>
		<option value="ARA">Arabidopsis</option>
		</select>
		</p>
		
		<p>Gene ID Type Requested<br>
		<select name="selectType" id="selectType">
		<option value="ecocycID" selected>Frame ID</option>
		<option value="commonName">Common Name</option>
		<option value="bnum">B number</option>
		<option value="synonym">Synonym</option>
		<option value="probesetId">Probeset ID</option>
		</select>
		</p>
		
		Genes: <input type="text" name="geneIDs" id="geneIDs" />
	
	<input name="submit" type="submit" value="submit" />
	</form>
	
	<p><bold>Instructions:</bold></p>
	<p>
	Any single gene can be put into the text box.  You can select an organism and the type of ID you would like back. 
	The system automatically determines the type of ID you put in, searches for the gene in the selected organism database, and returns the results.</p>
	<p>
	Multiple gene inputs can be used as well.  They must be comma separated with no spacing, and can be placed in the text box the same as a single gene.</p>
	<p>
	Probe mappings are currently only available for Ecoli.  "B numbers" are strictly an Ecoli identifier type. 
	Other current known issues are that genes with many synonyms return only the first match, and probe-gene pairs only return one 
	match if multiple matches are possible.  Many genes have no synonyms, which return empty.</p>
	<p>
	There is some limited "search-ability" or pattern matching when querying genes.  If you search for the gene "b31" in Ecoli, your 
	results include all genes that match "b31##", but if you search "31", you get very different results.</p>
	';
}
?>

</body>
</html>