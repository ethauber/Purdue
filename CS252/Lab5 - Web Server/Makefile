CXX = g++ -fPIC
NETLIBS= -lnsl

all: daytime-server daytime-client 

daytime-client : daytime-client.o
	$(CXX) -o $@ $@.o $(NETLIBS)

daytime-server : daytime-server.o
	$(CXX) -pthread -o $@ $@.o $(NETLIBS)

%.o: %.cc
	@echo 'Building $@ from $<'
	$(CXX) -o $@ -c -I. $<

clean:
	rm -f *.o daytime-server daytime-client 

