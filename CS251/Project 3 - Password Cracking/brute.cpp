#include <cstdlib>
#include <fstream>
#include <iostream>
#include <string>

#include "key.hpp"
#include "brute.hpp"
//#include "timer.hpp"

std::string me;
std::string encrypted;
std::string table_filename;
bool verbose = false;

//std::string guess;
//CPU_timer t;
unsigned long long total = 0;
unsigned long long LIMIT = pow(32,C);

//std::string Brute::genString() {//generate string
//    std::string genS = "";
//    unsigned long long tempTotal = total; 
//    for(int i = 0; i < C; i++) {
//        genS.insert(genS.end()-i, ALPHABET[tempTotal%32]);
//        tempTotal = tempTotal >> 5;
//    }
//    total++;
//    return genS;
//}

Brute::Brute(const std::string& filename) {
	T.resize(N);
	std::string buffer;
    std::fstream input(filename.c_str(), std::ios::in);
    for (int i = 0; i < N; i++) {
        std::getline(input, buffer);
        T[i].set_string(buffer);
    }
    input.close();
}

void Brute::decrypt(const std::string& encrypted){
	// your code here
 	unsigned long long tempTotal;
 	Key encryptedK = Key(encrypted);
 	word_type guess;
 	for(int i = 0; i < C; i++) guess[i] = 0;
 	//Key(guess).show(); print aa...aa
 	//t.tic();
 	for(unsigned long long i = 4000; i < LIMIT; i++) {
     	//guess = genString();
     	tempTotal = total;
     	for(int j = 0; j < C; j++) {
     		guess[j] = tempTotal%32;
         	//std::cout << ALPHABET[tempTotal%32];
         	tempTotal = tempTotal >> B;
        }
     	total++;
        Key ss;
     	ss = Key(guess).subset_sum(T, false);
     	if(encryptedK == ss) {
     		//std::cout << guess << std::endl;
         	Key(guess).showS();
        }
    }
 	//t.toc();
 	//std::cout << "time for C=" << C << " is:" << t.elapsed() << std::endl;
}

void usage(const std::string& error_msg="") {
	if (!error_msg.empty()) std::cout << "ERROR: " << error_msg << '\n';
	std::cout << me << ": Brute force cracking of Subset-sum password with " 
		<< B << " bits precision\n"
	    << "USAGE: " << me << " <encrypted> <table file> [options]\n"
		<< "\nArguments:\n"
		<< " <encrypted>:   encrypted password to crack\n"
		<< " <table file>:  name of file containing the table to use\n"
		<< "\nOptions:\n"
		<< " -h|--help:     print this message\n"
		<< " -v|--verbose:  select verbose mode\n\n";
	exit(0);
}

void initialize(int argc, char* argv[]) {
	me = argv[0];
	if (argc < 3) usage("Missing arguments");
	encrypted = argv[1];
	table_filename = argv[2];
	for (int i=3; i<argc; ++i) {
		std::string arg = argv[i];
		if (arg == "-h" || arg == "--help") usage();
		else if (arg == "-v" || arg == "--verbose") verbose = true;
		else usage("Unrecognized argument: " + arg);
	}
}

int main(int argc, char *argv[]){
	initialize(argc, argv);
	// your code here
 	Brute b(table_filename);
 	b.decrypt(encrypted);
 	return 0;
}