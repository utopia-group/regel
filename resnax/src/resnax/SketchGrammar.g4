grammar SketchGrammar;


sketch : 
  program   # Prog
| var       # FreeVarSketch
| var '{' lsketch '}'       # GuardedVarSketch
| 'sketch(' sketch ',' INT ')'  # RepSketch
| 'startwith(' sketch ')'   # StartwithSketch
| 'endwith(' sketch ')'     # EndwithSketch
| 'contain(' sketch ')'      # ContainSketch
| 'optional(' sketch ')'      # OptionalSketch
| 'star(' sketch ')'            # StarSketch
| 'repeat(' sketch ',' INT ')'      # RepeatSketch
| 'repeatatleast(' sketch ',' INT ')'	# RepeatAtLeastSketch
| 'repeatrange(' sketch ',' INT ',' INT ')'      # RepeatRangeSketch
| 'concat(' sketch ',' sketch ')'    # ConcatSketch 
| 'not(' sketch ')'            # NotSketch
| 'notcc(' sketch ')'           #NotCCSketch
| 'and(' sketch ',' sketch ')'    # AndSketch
| 'or(' sketch ',' sketch ')'     # OrSketch
| 'sep(' sketch ',' sketch ')'      # SepSketch
;



lsketch : 
  sketch        # SingleSketch
| sketch ',' lsketch     # MultiSketch
;



var: 
  '?'
;



program: 
  CC        # CharClassProg
| CONST		# ConstantProg
| 'null'    # NullProg
| 'eps'     # EmptyProg
| 'startwith(' program ')'   # StartwithProg
| 'endwith(' program ')'     # EndwithProg 
| 'contain(' program ')'      # ContainProg
| 'optional(' program ')'       # OptionalProg
| 'star(' program ')'           # StarProg
| 'repeat(' program ',' INT ')'      # RepeatProg 
| 'repeat(' program ',' INT ',)'      # RepeatAtLeastProg
| 'repeat(' program ',' INT ',' INT ')'      # RepeatRangeProg
| 'concat(' program ',' program ')'    # ConcatProg 
| 'not(' program ')'            # NotProg
| 'notcc(' program ')'          # NotCCProg
| 'and(' program ',' program ')'    # AndProg 
| 'or(' program ',' program ')'     # OrProg
| 'sep(' program ',' program ')'    # SepProg
;


CC: 
  '<num>'
| '<num1-9>'
| '<cap>'
| '<low>'
| '<vow>'
| '<let>'
| '<alphanum>'
| '<any>'
| '<hex>'
| '<spec>'
| '<m0>'
| '<m1>'
| '<m2>'
| '<m3>'
;

CONST:
  '<.>'
| '<,>'
| '< >'
| '<_>'
| '<+>'
| '<->'
| '<*>'
| '<#>'
| '<=>'
| '<^>'
| '<;>'
| '<:>'
| '<%>'
| '<@>'
| '<|>'
| '</>'
| '<\\>'
| '<&>'
| '<(>'
| '<)>'
| '<<>'
| '<>>'
| '<$>'
| '<!>'
| '<?>'
| '<~>'
| '<{>'
| '<}>'
| '<0>'
| '<1>'
| '<2>'
| '<3>'
| '<4>'
| '<5>'
| '<6>'
| '<7>'
| '<8>'
| '<9>'
| '<A>'
| '<B>'
| '<C>'
| '<D>'
| '<E>'
| '<F>'
| '<G>'
| '<H>'
| '<I>'
| '<J>'
| '<K>'
| '<L>'
| '<M>'
| '<N>'
| '<O>'
| '<P>'
| '<Q>'
| '<R>'
| '<S>'
| '<T>'
| '<U>'
| '<V>'
| '<W>'
| '<X>'
| '<Y>'
| '<Z>'
| '<a>'
| '<b>'
| '<c>'
| '<d>'
| '<e>'
| '<f>'
| '<g>'
| '<h>'
| '<i>'
| '<j>'
| '<k>'
| '<l>'
| '<m>'
| '<n>'
| '<o>'
| '<p>'
| '<q>'
| '<r>'
| '<s>'
| '<t>'
| '<u>'
| '<v>'
| '<w>'
| '<x>'
| '<y>'
| '<z>'
;

INT: [0-9]+;


