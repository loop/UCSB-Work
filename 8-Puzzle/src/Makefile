JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Puzzle.java \

default: classes

all: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class