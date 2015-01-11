.PHONY : default clean realclean full

SRC = src/
MAIN       = cv_7.tex
FILE_OUT   = Rapport

default :
	@cd $(SRC); make full
	@cd $(SRC); mv $(FILE_OUT).pdf ..

clean:
	@cd $(SRC); make clean

realclean:
	@make clean
	@cd $(SRC); make realclean
#	find . -name "*~" -exec $(RM) {} \; -or -name "\#*\#*" -exec $(RM) {} \;
	$(RM) -f $(FILE_OUT).pdf

# index:
#       makeindex -s src/rapport.ist tmp/rapport.idx

# rapport:
# 	@cd $(SRC) && mkdir -p tmp/src
# 	@cd $(SRC) && \pdflatex --jobname $(FILE_OUT) --output-directory=tmp/ $(MAIN)

# full:
# 	@make rapport
# 	@cd $(SRC) && mv -f tmp/$(FILE_OUT).pdf ../$(FILE_OUT).pdf
