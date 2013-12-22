rng('shuffle');
    p=[30/55 10/55 15/55];

    s=size(dataset,1);
    p=round(cumsum(p)*s);


prestazioni=[]
for a=1:100
    a
    dataset = dataset(randperm(s),:);

    trainingset=dataset(1:p(1),:);
    scoringset=dataset((1+p(1)):p(2),:);
    testset=dataset((1+p(2)):end,:);
    tree = ClassificationTree.fit(trainingset(:,1:13),trainingset(:,14));
    risultati = predict(tree,testset(:,1:13));

    pr=confusion_f( 1+testset(:,14), 1+risultati)
    prestazioni=[prestazioni pr];
end
prestazioni