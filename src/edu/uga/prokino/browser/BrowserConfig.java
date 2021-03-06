package edu.uga.prokino.browser;

import java.util.HashMap;

public class BrowserConfig
{
    public final static String                             ontologyServerURL      = "jdbc:virtuoso://128.192.62.241:1111";
    public final static String                             sparqlServiceURL       = "http://128.192.62.241:8890/sparql";
    public final static String                             prokinoGraphURI        = "http://prokino.uga.edu";

    //public final static String                             ontologyServerURL      = "jdbc:virtuoso://vulcan.cs.uga.edu:8088";
    public final static String                             ontologyServerGraph    = "http://prokino.uga.edu";
    public final static String                             ontologyServerUser     = "dba";
    public final static String                             ontologyServerPassword = "dba";
    
    private static HashMap<String, String>                 fwdDisplayName         = null;
    private static HashMap<String, String>                 bckDisplayName         = null;

    private static HashMap<String, ClassDisplayProperties> classDisplayProperties = null;

    public static final String[]                           mutationTypes          = { "ComplexMutation",
										      "ComplexDeletionInframe",
										      "ComplexFrameshift",
										      "ComplexInsertionInframe",
										      "CompoundSubstitution",
										      "DeletionMutation",
										      "DeletionFrameshift",
										      "DeletionInframe",
										      "InsertionMutation",
										      "InsertionFrameshift",
										      "InsertionInframe",
										      "SubstitutionMutation",
										      "CodingSilent",
										      "Missense",
										      "Nonsense",
										      "Fusion",
										      "OtherMutation"                                                    
  };

    static {

        fwdDisplayName = new HashMap<String, String>();
        bckDisplayName = new HashMap<String, String>();

        classDisplayProperties = new HashMap<String, ClassDisplayProperties>();
        ClassDisplayProperties properties = null;

        // Clade display properties
        //
        properties = new ClassDisplayProperties( "Clade" );
        classDisplayProperties.put( "Clade", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "isPartOf" );

        // Organism display properties
        //
        properties = new ClassDisplayProperties( "Organism" );
        classDisplayProperties.put( "Organism", properties );
        properties.addDataProperty( "hasCommonName" );
        properties.addDataProperty( "hasScientificName" );

        properties.addObjectProperty( "isPartOf" );
        properties.addObjectProperty( "presentIn" );
        properties.addObjectProperty( "hasDbXref" );

        // Gene display properties
        //
        properties = new ClassDisplayProperties( "Gene" );
        classDisplayProperties.put( "Gene", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasUniprotPrimaryName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "hasCellularLocation" );
        properties.addDataProperty( "hasTissueSpecificity" );
        properties.addDataProperty( "chromosomalPosition" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "presentIn" );
        properties.addObjectProperty( "hasFunctionalDomain" );
        properties.addObjectProperty( "hasNterminus" );
        properties.addObjectProperty( "hasSubDomain" );
        properties.addObjectProperty( "hasCterminus" );
        properties.addObjectProperty( "hasFunctionalFeature" );
        properties.addObjectProperty( "hasProteinStructure" );
        properties.addObjectProperty( "hasSequence" );
        properties.addObjectProperty( "associatedWith" );
        properties.addObjectProperty( "participatesIn" );
        properties.addObjectProperty( "consumes" );
        properties.addObjectProperty( "produces" );
        properties.addObjectProperty( "includes" );
        properties.addObjectProperty( "hasMutation" );
        properties.addObjectProperty( "hasDbXref" );

        // Sequence display properties
        //
        properties = new ClassDisplayProperties( "Sequence" );
        classDisplayProperties.put( "Sequence", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "hasFASTAFormat" );
        properties.addDataProperty( "hasIsoformName" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasNterminus" );
        properties.addObjectProperty( "hasMotif" );
        properties.addObjectProperty( "hasCterminus" );
        properties.addObjectProperty( "hasFunctionalFeature" );
        properties.addObjectProperty( "hasProteinStructure" );
        properties.addObjectProperty( "locatedIn" );
        properties.addObjectProperty( "hasSequence" );
        properties.addObjectProperty( "hasDbXref" );

        // Structure display properties
        //
        properties = new ClassDisplayProperties( "Structure" );
        classDisplayProperties.put( "Structure", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "comment" );
        properties.addDataProperty( "hasPKDomain" );

        properties.addObjectProperty( "hasProteinStructure" );
        properties.addObjectProperty( "hasDbXref" );

        // Mutation display properties
        //
        properties = new ClassDisplayProperties( "Mutation" );
        classDisplayProperties.put( "Mutation", properties );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "hasMutationId" );
        properties.addDataProperty( "hasFusionId" );
        properties.addDataProperty( "hasFusionDescription" );
        properties.addDataProperty( "fusedWith" );
        properties.addDataProperty( "hasMutationAA" );
        properties.addDataProperty( "hasSampleId" );
        properties.addDataProperty( "hasSampleName" );
        properties.addDataProperty( "hasSampleSource" );
        properties.addDataProperty( "hasMutationZygosity" );
        properties.addDataProperty( "hasSomaticStatus" );
        properties.addDataProperty( "hasTumorOrigin" );
        properties.addDataProperty( "hasPrimarySite" );
        properties.addDataProperty( "hasStartLocation" );
        properties.addDataProperty( "hasEndLocation" );
        properties.addDataProperty( "hasPKAStartLocation" );
        properties.addDataProperty( "hasPKAEndLocation" );
        properties.addDataProperty( "hasPKAResidue" );
        properties.addDataProperty( "hasSiteSubType" );
        properties.addDataProperty( "hasMutationImpact" );
        properties.addDataProperty( "comment" );
        properties.addDataProperty( "hasComment" );

        properties.addObjectProperty( "fusedWith" );
        properties.addObjectProperty( "locatedIn" );
        properties.addObjectProperty( "implicatedIn" );
        properties.addObjectProperty( "hasMutation" );
        properties.addObjectProperty( "inSample" );
        properties.addObjectProperty( "hasDbXref" );

        // Sample display properties
        //
        properties = new ClassDisplayProperties( "Sample" );
        classDisplayProperties.put( "Sample", properties );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "hasSampleId" );
        properties.addDataProperty( "hasSampleName" );
        properties.addDataProperty( "hasSampleSource" );
        properties.addDataProperty( "hasMutationZygosity" );
        properties.addDataProperty( "hasSomaticStatus" );
        properties.addDataProperty( "hasTumorOrigin" );
        properties.addDataProperty( "hasPrimarySite" );
        properties.addDataProperty( "hasSiteSubType" );
        properties.addDataProperty( "comment" );
        properties.addDataProperty( "hasComment" );

        properties.addObjectProperty( "hasSample" );
        properties.addObjectProperty( "hasDbXref" );

        // TopologicalDomain display properties
        //
        properties = new ClassDisplayProperties( "TopologicalDomain" );
        classDisplayProperties.put( "TopologicalDomain", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "hasStartLocation" );
        properties.addDataProperty( "hasEndLocation" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasFunctionalFeature" );
        properties.addObjectProperty( "hasDbXref" );

        // ModifiedResidue display properties
        //
        properties = new ClassDisplayProperties( "ModifiedResidue" );
        classDisplayProperties.put( "ModifiedResidue", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "hasPosition" );
        properties.addDataProperty( "hasStartLocation" );
        properties.addDataProperty( "hasEndLocation" );
        properties.addDataProperty( "hasModifiedResidueType" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasFunctionalFeature" );
        properties.addObjectProperty( "hasDbXref" );

        // SignalPeptide display properties
        //
        properties = new ClassDisplayProperties( "SignalPeptide" );
        classDisplayProperties.put( "SignalPeptide", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "hasStartLocation" );
        properties.addDataProperty( "hasEndLocation" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasFunctionalFeature" );
        properties.addObjectProperty( "hasDbXref" );

        // FunctionalDomain display properties
        //
        properties = new ClassDisplayProperties( "FunctionalDomain" );
        classDisplayProperties.put( "FunctionalDomain", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        // properties.addDataProperty( "label" );

        properties.addObjectProperty( "hasFunctionalDomain" );
        properties.addObjectProperty( "hasDbXref" );

        // SequenceMotif display properties
        //
        properties = new ClassDisplayProperties( "SequenceMotif" );
        classDisplayProperties.put( "SequenceMotif", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "hasStartLocation" );
        properties.addDataProperty( "hasEndLocation" );
        properties.addDataProperty( "hasPKAStartLocation" );
        properties.addDataProperty( "hasPKAEndLocation" );
        properties.addDataProperty( "hasMotifSequence" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasMotif" );
        properties.addObjectProperty( "contains" );
        properties.addObjectProperty( "hasDbXref" );


        // StructuralMotif display properties
        //
        properties = new ClassDisplayProperties( "StructuralMotif" );
        classDisplayProperties.put( "StructuralMotif", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
	//        properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasMotif" );
        properties.addObjectProperty( "contains" );
        properties.addObjectProperty( "hasDbXref" );

        // Pathway display properties
        //
        properties = new ClassDisplayProperties( "Pathway" );
        classDisplayProperties.put( "Pathway", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        // properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasReaction" );
        properties.addObjectProperty( "hasParentPathway" );
        properties.addObjectProperty( "participatesIn" );
        properties.addObjectProperty( "hasDbXref" );

        // Reaction display properties
        //
        properties = new ClassDisplayProperties( "Reaction" );
        classDisplayProperties.put( "Reaction", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        // properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasCatalyst" );
        properties.addObjectProperty( "consumes" );
        properties.addObjectProperty( "produces" );
        properties.addObjectProperty( "precededByReaction" );
        //properties.addObjectProperty( "hasParentPathway" );
        properties.addObjectProperty( "hasReaction" );
        properties.addObjectProperty( "hasDbXref" );

        // CatalystActivity display properties
        //
        properties = new ClassDisplayProperties( "CatalystActivity" );
        classDisplayProperties.put( "CatalystActivity", properties );
        properties.addDataProperty( "hasPrimaryName" );
        // properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "hasCatalyst" );
        properties.addObjectProperty( "hasDbXref" );

        // Complex display properties
        //
        properties = new ClassDisplayProperties( "Complex" );
        classDisplayProperties.put( "Complex", properties );
        properties.addDataProperty( "hasPrimaryName" );
        // properties.addDataProperty( "label" );
        // properties.addDataProperty( "comment" );

        properties.addObjectProperty( "includes" );
        properties.addObjectProperty( "hasDbXref" );

        // SmallMoleculeEntity display properties
        //
        properties = new ClassDisplayProperties( "SmallMoleculeEntity" );
        classDisplayProperties.put( "SmallMoleculeEntity", properties );
        properties.addDataProperty( "hasPrimaryName" );
        // properties.addDataProperty( "label" );
        // properties.addDataProperty( "comment" );
        properties.addObjectProperty( "includes" );
        properties.addObjectProperty( "hasCatalyst" );
        properties.addObjectProperty( "consumes" );
        properties.addObjectProperty( "produces" );
        properties.addObjectProperty( "hasDbXref" );

        // GenomeEncodedEntity display properties
        //
        properties = new ClassDisplayProperties( "GenomeEncodedEntity" );
        classDisplayProperties.put( "GenomeEncodedEntity", properties );
        properties.addDataProperty( "hasPrimaryName" );
        // properties.addDataProperty( "label" );
        // properties.addDataProperty( "comment" );
        properties.addObjectProperty( "includes" );
        properties.addObjectProperty( "hasCatalyst" );
        properties.addObjectProperty( "consumes" );
        properties.addObjectProperty( "produces" );
        properties.addObjectProperty( "hasDbXref" );

        // EntitySet display properties
        //
        properties = new ClassDisplayProperties( "EntitySet" );
        classDisplayProperties.put( "EntitySet", properties );
        properties.addDataProperty( "hasPrimaryName" );
        // properties.addDataProperty( "label" );
        // properties.addDataProperty( "comment" );

        properties.addObjectProperty( "includes" );
        properties.addObjectProperty( "hasDbXref" );

        // OtherEntity display properties
        //
        properties = new ClassDisplayProperties( "OtherEntity" );
        classDisplayProperties.put( "OtherEntity", properties );
        properties.addDataProperty( "hasPrimaryName" );
        // properties.addDataProperty( "label" );
        // properties.addDataProperty( "comment" );

        properties.addObjectProperty( "includes" );
        properties.addObjectProperty( "hasDbXref" );

        // Cancer display properties
        //
        properties = new ClassDisplayProperties( "Cancer" );
        classDisplayProperties.put( "Cancer", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "hasFullName" );
        properties.addDataProperty( "hasOtherName" );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "associatedWith" );
        properties.addObjectProperty( "implicatedIn" );
        properties.addObjectProperty( "hasDbXref" );

        // Group display properties
        //
        properties = new ClassDisplayProperties( "Group" );
        classDisplayProperties.put( "Group", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

	//        properties.addObjectProperty( "subClassOf" );

        // Family display properties
        //
        properties = new ClassDisplayProperties( "Family" );
        classDisplayProperties.put( "Family", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "codedBy" );
        properties.addObjectProperty( "presentIn" );
	//        properties.addObjectProperty( "subClassOf" );

        // SubFamily display properties
        //
        properties = new ClassDisplayProperties( "Subfamily" );
        classDisplayProperties.put( "Subfamily", properties );
        properties.addDataProperty( "hasPrimaryName" );
        properties.addDataProperty( "label" );
        properties.addDataProperty( "comment" );

        properties.addObjectProperty( "codedBy" );
        properties.addObjectProperty( "presentIn" );
	//        properties.addObjectProperty( "subClassOf" );

        // Display names for outgoing (forward) and incoming (backward) property
        // names
        //

        // data properties (only forward)
        //
        fwdDisplayName.put( "chromosomalPosition", "Chromosomal Position:" );
        fwdDisplayName.put( "hasCellularLocation", "Cellular Location:" );
        fwdDisplayName.put( "hasFullName", "Full Name:" );
        fwdDisplayName.put( "hasOtherName", "Other Names:" );
        fwdDisplayName.put( "hasPrimaryName", "Primary Name:" );
        fwdDisplayName.put( "hasUniprotPrimaryName", "UniProt Name:" );
        fwdDisplayName.put( "hasTissueSpecificity", "Tissue Specificity:" );
        fwdDisplayName.put( "comment", "Description:" );
        fwdDisplayName.put( "label", "Official Name:" );
        fwdDisplayName.put( "hasCancerType", "Cancer Type:" );
        fwdDisplayName.put( "hasMutantType", "Mutant Type:" );
        fwdDisplayName.put( "hasMutatioPosition", "Mutation Position:" );
        fwdDisplayName.put( "hasMutationPosition", "Mutation Position:" );
        fwdDisplayName.put( "hasMutationAA", "Mutation AA:" );
        fwdDisplayName.put( "hasMutationDescription", "Mutation Description:" );
        fwdDisplayName.put( "hasMutationId", "Mutation Id:" );
	//        fwdDisplayName.put( "hasFusionId", "Fusion Id:" );
	// this should be changed in the ontology to hasMutationId
        fwdDisplayName.put( "hasFusionId", "Mutation Id:" );
        fwdDisplayName.put( "hasFusionDescription", "Fusion Description:" );
        fwdDisplayName.put( "fusedWith", "Fused With:" );
        fwdDisplayName.put( "hasSampleSource", "Sample Source:" );
        fwdDisplayName.put( "hasMutationZygosity", "Mutation Zygosity:" );
        fwdDisplayName.put( "hasSomaticStatus", "Somatic Status:" );
        fwdDisplayName.put( "hasTumorOrigin", "Tumor Origin:" );
        fwdDisplayName.put( "hasComment", "Comment:" );
        fwdDisplayName.put( "hasPrimarySite", "Primary Site:" );
        fwdDisplayName.put( "hasSiteSubType", "Site Sub-type:" );
        fwdDisplayName.put( "hasPubMedPMID", "PubMed PMID:" );
        fwdDisplayName.put( "hasWildTypeResidue", "Wild Type Residue:" );
        fwdDisplayName.put( "hasEndLocation", "End Location:" );
        fwdDisplayName.put( "hasStartLocation", "Start Location:" );
        fwdDisplayName.put( "hasPKAEndLocation", "PKA End Location:" );
        fwdDisplayName.put( "hasPKAStartLocation", "PKA Start Location:" );
        fwdDisplayName.put( "hasPKAResidue", "PKA Residue:" );
        fwdDisplayName.put( "hasMotifSequence", "Motif Sequence:" );
        fwdDisplayName.put( "hasScientificName", "Scientific Name:" );
        fwdDisplayName.put( "hasCommonName", "Common Name:" );
        fwdDisplayName.put( "hasPosition", "Position:" );
        fwdDisplayName.put( "hasFASTAFormat", "FASTA Format:" );
        fwdDisplayName.put( "hasModifiedResidueType", "Modified Residue Type:" );
        fwdDisplayName.put( "hasSampleId", "Sample Id:" );
        fwdDisplayName.put( "hasSampleName", "Sample Name:" );
        fwdDisplayName.put( "hasIsoformName", "Isoform Name:" );
        fwdDisplayName.put( "hasMutationImpact", "Mutation Impact:" );
        fwdDisplayName.put( "hasPKDomain", "Has PK Domain:" );

        // object properties
        //

        // meta properties (rdf/rdfs)
        //
        fwdDisplayName.put( "subClassOf", "Parent Class:" );
        bckDisplayName.put( "subClassOf", "Sub-classes:" );

        // other properties
        //
        fwdDisplayName.put( "isPartOf", "Clade:" );
        bckDisplayName.put( "isPartOf", "Organisms:" );

        fwdDisplayName.put( "presentIn", "Present In:" );
        bckDisplayName.put( "presentIn", "Has Genes:" );

        fwdDisplayName.put( "codedBy", "Coded by Genes:" );
        bckDisplayName.put( "codedBy", "Codes for Domains:" );

        fwdDisplayName.put( "associatedWith", "Associated w/ Diseases:" );
        bckDisplayName.put( "associatedWith", "Correlates w/ Genes:" );

        fwdDisplayName.put( "hasFunctionalDomain", "Functional Domains:" );
        bckDisplayName.put( "hasFunctionalDomain", "Genes w/ this Domain:" );

        fwdDisplayName.put( "hasFunctionalFeature", "Functional Features:" );
        bckDisplayName.put( "hasFunctionalFeature", "Included Genes:" );

        fwdDisplayName.put( "hasMutation", "Mutations:" );
        bckDisplayName.put( "hasMutation", "Present in:" );

        fwdDisplayName.put( "hasSample", "Located in Sample:" );
        bckDisplayName.put( "hasSample", "Identified Mutation:" );

        fwdDisplayName.put( "inSample", "Located in Sample:" );
        bckDisplayName.put( "inSample", "Identified Mutation:" );

        fwdDisplayName.put( "hasProteinStructure", "PDB Structure:" );
        bckDisplayName.put( "hasProteinStructure", "Sequences with this PDB Structure:" );

        fwdDisplayName.put( "hasSequence", "Sequence:" );
        bckDisplayName.put( "hasSequence", "Sequence of:" );

        fwdDisplayName.put( "hasMotif", "Motifs:" );
        bckDisplayName.put( "hasMotif", "Motif of:" );

        fwdDisplayName.put( "participatesIn", "Participates in Pathways:" );
        bckDisplayName.put( "participatesIn", "Involved Genes:" );

        fwdDisplayName.put( "consumes", "Consumes:" );
        bckDisplayName.put( "consumes", "Consumed by Reactions:" );

        fwdDisplayName.put( "produces", "Produces:" );
        bckDisplayName.put( "produces", "Produced by Reactions:" );

        fwdDisplayName.put( "includes", "Elements:" );
        bckDisplayName.put( "includes", "Element of:" );

        fwdDisplayName.put( "contains", "Contains:" );
        bckDisplayName.put( "contains", "Contained in:" );

        fwdDisplayName.put( "implicatedIn", "Implicated in:" );
        bckDisplayName.put( "implicatedIn", "Related Mutations:" );

        fwdDisplayName.put( "hasReaction", "Reactions:" );
        bckDisplayName.put( "hasReaction", "Parent Pathway:" );

        fwdDisplayName.put( "hasCatalyst", "Catalyst:" );
        bckDisplayName.put( "hasCatalyst", "Catalyst in:" );

        fwdDisplayName.put( "hasParentPathway", "Parent Pathways:" );
        bckDisplayName.put( "hasParentPathway", "Sub-Pathways:" );

        fwdDisplayName.put( "precededByReaction", "Preceded by:" );
        bckDisplayName.put( "precededByReaction", "Followed by:" );

        fwdDisplayName.put( "hasDbXref", "External References:" );
        bckDisplayName.put( "hasDbXref", "External Reference of:" );

        fwdDisplayName.put( "locatedIn", "Located in:" );
        bckDisplayName.put( "locatedIn", "Location of:" );

        fwdDisplayName.put( "hasNterminus", "Nterminus:" );
        bckDisplayName.put( "hasNterminus", "Nterminus of:" );

        fwdDisplayName.put( "hasCterminus", "Cterminus:" );
        bckDisplayName.put( "hasCterminus", "Cterminus of:" );

        fwdDisplayName.put( "parent", "Parent:" );
        bckDisplayName.put( "parent", "Descendants:" );

        // special mutation property names
        //
        fwdDisplayName.put( "ComplexMutation", "Complex Mutation:" );
        fwdDisplayName.put( "ComplexDeletionInframe",
                "Complex Deletion in Frame:" );
        fwdDisplayName.put( "ComplexFrameshift", "Complex Frameshift:" );
        fwdDisplayName.put( "ComplexInsertionInframe",
                "Complex Insertion in Frame:" );
        fwdDisplayName.put( "CompoundSubstitution", "Compound Substitution:" );
        fwdDisplayName.put( "DeletionMutation", "Deletion Mutation:" );
        fwdDisplayName.put( "DeletionFrameshift", "Deletion Frameshift:" );
        fwdDisplayName.put( "DeletionInframe", "Deletion in Frame:" );
        fwdDisplayName.put( "InsertionMutation", "Insertion Mutation:" );
        fwdDisplayName.put( "InsertionFrameshift", "Insertion Frameshift:" );
        fwdDisplayName.put( "InsertionInframe", "Insertion in Frame:" );
        fwdDisplayName.put( "SubstitutionMutation", "Substitution Mutation:" );
        fwdDisplayName.put( "CodingSilent", "Coding Silent:" );
        fwdDisplayName.put( "Missense", "Missense:" );
        fwdDisplayName.put( "Nonsense", "Nonsense:" );
        fwdDisplayName.put( "Fusion", "Fusion:" );
        fwdDisplayName.put( "OtherMutation", "Other Mutation:" );

    }

    // return the DisplayProperties for a given class
    //
    public static ClassDisplayProperties getDisplayProperties(String className)
    {
        return classDisplayProperties.get( className );
    }

    // return the display value for a given property, either forward (outgoing)
    // or backward (incoming)
    //
    public static String getPropertyDisplayName(String name, Boolean forward)
    {
        if( forward ) {
            if( fwdDisplayName.containsKey( name ) )
                return fwdDisplayName.get( name );
            else
                return name + "->";
        }
        else {
            if( bckDisplayName.containsKey( name ) )
                return bckDisplayName.get( name );
            else
                return "<-" + name;
        }
    }

}
