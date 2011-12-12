<html>
<head>
	<title>Gene Point Structure Locator</title>
</head>
<body>

<?php
if (isset($_POST['genePoints'])) {
	findStructures($_POST['selectType'],$_POST['genePoints'],($_POST['selectOrganism']));
} else {
	print_form();
}

function findStructures($StructureType,$ListOfGenePoints,$Organism)
{
$cmd = ("java -jar GenePoints.jar $StructureType $ListOfGenePoints $Organism");
        exec($cmd, $output, $ret);
        foreach ($output as $value) {
                echo $value;
                echo "<br />";
        }
        if (count($output) == 0) {
        	echo "Failed to run query with given parameters";
        	echo $cmd;
        }
}

function print_form() {
	echo '
	<form action="genePoints.php" method="post">
	
		<p>Organism<br>
		<select name="selectOrganism" id="selectOrganism">
		<option value="ECOLI" selected>E. coli</option>
		<option value="META">MetaCyc</option>
		<option value="ARA">Arabidopsis</option>
		</select>
		</p>
		
		<p>Gene Structure Type Requested<br>
		<select name="selectType" id="selectType">
		<option value="AllTypes" selected>All Available Structure Types</option>
		<option value="All-Genes">All Genes</option>
		<option value="Rho-Independent-Terminators">Rho Independent Terminators</option>
		</select>
		</p>
		
		GenePoints: <input type="text" name="genePoints" id="genePoints" />
	
	<input name="submit" type="submit" value="submit" />
	</form>
	
	<p><bold>Instructions:</bold></p>
	<p>
	The purpose of this tool is to identify any genomic structures that exist at a given nucleotide position.  For example, you have just run a reassembly of the
	E. coli genome and have discovered various SNPs and their locations.  What genes or other structures did those SNPs affect?  Just enter the locations of those
	SNPs here and find out! </p>
	<p>
	Any single integer nucleotide position or a comma-separated list of integer positions can be put into the GenePoints text box.  You can select an organism and
	the type of structures you would like to search.</p>
	<p>
	For example, you can select the organism "E. coli", search "All Available Structure Types", and choose the gene points, the expected output would be: </p>
	<p>
		All-Genes: 4606 <br>
		Rho-Independent-Terminators: 233 <br>
		Processing points: 3 <br>
		POINT COMMON-NAME ECOCYC-ID LEFT-END-POSITION RIGHT-END-POSITION <br>
		3056667 TERM0661 TERM0661 3056666 3056682 <br>
		4026805 fadB EG10279 4026805 4028994 <br>
		4159090 fabR EG11394 4159090 4159794</p>
	<p>
	The first few rows tell you what types of structures you have searched and how many such structures exist in the database.  The row labeled "Processing points:"
	shows how many nucleotide positions you are searching.  The results are then printed in a tab-delimited format.</p>
	<p>
	Note: please allow up to several minutes for the results to show.  While the program scales well to large numbers of input points, there is about a 60 second
	"start-up" time to load the backend database, even for checking a single point. </p>
	';
}
?>

</body>
</html>