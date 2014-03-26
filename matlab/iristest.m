load fisheriris
gscatter(meas(:,1), meas(:,2), species,'rgb','osd');
xlabel('Sepal length');
ylabel('Sepal width');
N = size(meas,1);
ldaClass = classify(meas(:,1:2),meas(:,1:2),species);
bad = ~strcmp(ldaClass,species);
ldaResubErr = sum(bad) / N
[ldaResubCM,grpOrder] = confusionmat(species,ldaClass)
figure
hold on;
plot(meas(bad,1), meas(bad,2), 'kx');
hold off;
figure
[x,y] = meshgrid(4:.1:8,2:.1:4.5);
x = x(:);
y = y(:);
j = classify([x y],meas(:,1:2),species);
gscatter(x,y,j,'grb')

figure;
gscatter(meas(:,3), meas(:,4), species,'rgb','osd');
xlabel('Petal length');
ylabel('Petal width');



figure;
gscatter(meas(:,4), meas(:,3), species,'rgb','osd');
xlabel('1Petal width');
ylabel('1Petal length');