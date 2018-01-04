import java.util.HashMap;

public final class WordNet {
 private HashMap<Integer, String> idToSynDef;
 private HashMap<String, Bag<Integer>> synToId;
 private SAP ssap;
 private Digraph diGraph;
 
 // constructor takes the name of the two input files
 public WordNet(String synsets, String hypernyms) {
  //todo
  In synIn = new In(synsets);
  In hypIn = new In(hypernyms);
  
  idToSynDef = new HashMap<Integer, String>();
  synToId = new HashMap<String, Bag<Integer>>();
  
  //fill the hashmaps (synsets)
  while (!synIn.isEmpty()) {
   String curln = synIn.readLine();
   String[] fields = curln.split(",");
   for (int i = 0; i < fields.length; i++) {
    fields[i] = fields[i].trim();
   }
   
   int id = Integer.parseInt(fields[0]);
   String synDef = fields[1];
   idToSynDef.put(id, synDef);
         
   String synonyms[] = fields[1].split(" ");
   for (int i = 0; i < synonyms.length; i++) {
    synonyms[i] = synonyms[i].trim();
    Bag<Integer> bag = synToId.get(synonyms[i]);
    if (bag == null) {
     Bag<Integer> newBag = new Bag<Integer>();
     newBag.add(id);
     synToId.put(synonyms[i], newBag);
    }
    else {
     bag.add(id);
    }
   }
  }
  
  //build digraph (hypernyms)
  diGraph = new Digraph(idToSynDef.size()+1);
  while (!hypIn.isEmpty()) {
   String curln = hypIn.readLine();
   String[] fields = curln.split(",");
   for (int i = 0; i < fields.length; i++) {
    fields[i] = fields[i].trim();
   }
   for (int i = 1; i < fields.length; i++) {
    diGraph.addEdge(Integer.parseInt(fields[0]), Integer.parseInt(fields[i]));
   }
  }
      
  ssap = new SAP(diGraph);
 
 }

 // is the word a WordNet noun? This can be used to search for existing
 // nouns at the beginning of the printSap method
 public boolean isNoun(String word) {
  //todo
  if(synToId.get(word) == null){
   StdOut.printf("sap = -1, ancestor = null\n");
   return false;
  }
  return true;
 }

 // print the synset (second field of synsets.txt) that is the common ancestor
 // of nounA and nounB in a shortest ancestral path as well as the length of the path,
 // following this format: "sap<space>=<space><number>,<space>ancestor<space>=<space><synsettext>"
 // If no such path exists the sap should contain -1 and ancestor should say "null"
 // This method should use the previously defined SAP datatype
 public void printSap(String nounA, String nounB) {
	if(!isNoun(nounA) || !isNoun(nounB)) return;
  	Iterable<Integer> nA = synToId.get(nounA);
  	Iterable<Integer> nB = synToId.get(nounB);
  	int len = ssap.length(nA, nB);
  	int Id = ssap.ancestor(nA, nB);
  	String[] nouns = idToSynDef.get(Id).split(" ");
	StdOut.printf("sap = %d, ancestor = %s\n", len, nouns[0]);
 }

 public static void main(String[] args) {
  In inInput = new In(args[2]);
  WordNet WN = new WordNet(args[0], args[1]);
  while(!inInput.isEmpty()) {
   String[] nouns = inInput.readLine().split(" ");
   WN.printSap(nouns[0], nouns[1]);
   //int len = s.length(v, w);
   //int anc = s.ancestor(v, w);
   //StdOut.printf("sap = %d, ancestor = %d\n", len, anc);
   
  }
 }
 
}