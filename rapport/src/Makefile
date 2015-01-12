.PHONY : default clean realclean full

MAIN_CLASS = cv_7.tex
FILE_OUT   = Rapport

default :
	make full

clean:
	$(RM) -rf tmp/

realclean:
	@make clean
	find . -name "*~" -exec $(RM) {} \; -or -name "\#*\#*" -exec $(RM) {} \;
	$(RM) -f $(FILE_OUT).pdf

# index:
#       makeindex -s src/rapport.ist tmp/rapport.idx

rapport:
	@mkdir -p tmp/
	\pdflatex --jobname $(FILE_OUT) --output-directory=tmp/ $(MAIN_CLASS)

full:
	@make rapport
	@mv -f tmp/$(FILE_OUT).pdf $(FILE_OUT).pdf
