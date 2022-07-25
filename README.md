# Java language API for NLP

## Installation of the NLP server
Runs on Linux and MacOS.
- Go to https://code.google.com/archive/p/mate-tools/downloads and download the following files:
    - srl-4.31.tgz (the full pipeline), unzip to srl-20131216
    - CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model
    - CoNLL2009-ST-English-ALL.anna-3.3.parser.model
    - CoNLL2009-ST-English-ALL.anna-3.3.postagger.model
    - CoNLL2009-ST-English-ALL.anna-3.3.srl.model
- Place the four CoNLL2009 models in the same folder, named e.g. models in the same folder as the pipeline srl-20131216
- In the folder srl-20131216/scripts there is a script called ``run_http_server.sh``, open the script and change the ``MODELDIR`` on line 15 to the path to the model directory.
- In a terminal, navigagte to the script forlder and make the shell script runnable with``chmod +x run_http_server.sh``.
- Start the server: `` ./run_http_server.sh``
- It takes a few seconds for the models to load, it finishes with the message: 
```
Server up and listening on port 8072
Setting up pipeline
done
```
- Go to a web browser to http://localhost:8072.
- Type a sentence, e.g., _Pick up the workpiece_ in the box and press **Parse**.

## Finding the predicate-argument structures in PropBank 
The PropBank can be found on github: https://propbank.github.io/. The verb index can be searched through:
1. https://verbs.colorado.edu/verb-index/, select search in the upper left corner, write the predicate (verb), e.g. *pick* (without numbers), select **Go** and then click on the *pick.v* under **PropBank**, which will link to the list of different *picks*. https://verbs.colorado.edu/propbank/framesets-english-aliases/pick.html.
2. The verb index is listed here: https://verbs.colorado.edu/propbank/framesets-english-aliases/, e.g., select the ``pick.html`` link directly from the list.

## The Java code
Start the local language server above.

The [Main-class](eclipse_workspace/languageAPI/src/se/lth/cs/main/Main.java) just call the [SemanticSubmitter](eclipse_workspace/languageAPI/src/se/lth/cs/semantics/SemanticSubmitter.java) that handles the input and output from the server in the method ``processEverything``. Java classes for the output from the server, e.g, [Sentence](eclipse_workspace/languageAPI/src/se/lth/cs/semparser/corpus/Sentence.java) and [Predicates](eclipse_workspace/languageAPI/src/se/lth/cs/semparser/corpus/Predicate.java) and [PredArgs](eclipse_workspace/languageAPI/src/se/lth/cs/semantics/PredArgs.java) can be found in the ``corpus`` and ``semantics`` packages.

