load fisheriris;

t = classregtree(meas(:,1:3),species,'names',{'SL' 'SW' 'PL'}) % 'PL' 'PW'

tipi=unique(species);
specienumerica=ones(size(species));

for a=1:numel(tipi)
    i=find(strncmp(species,tipi{a},4));
    specienumerica(i)=a;
end

t = ClassificationTree.fit(meas(:,1:3),specienumerica);


[x,y,z] = meshgrid(4:.5:8, 2:.5:4.5 , 1:.5:6.9);
x = x(:);
y = y(:);
z = z(:);
g = predict(t,[x y z]);

h = gscatter(x, y, g);
% for each unique group in 'g', set the ZData property appropriately
gu = 1:3;
for k = 1:numel(gu)
      set(h(k), 'ZData', z( g == gu(k) ));
end
view(3)