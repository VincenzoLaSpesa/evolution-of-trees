x = randn(200,1);
y = randn(200,1);
z = randn(200,1);
g = [1*ones(50,1); 2*ones(50,1); 3*ones(50,1); 4*ones(50,1); ];
% call GSCATTER and capture output argument (handles to lines)
h = gscatter(x, y, g);
% for each unique group in 'g', set the ZData property appropriately
gu = unique(g);
for k = 1:numel(gu)
      set(h(k), 'ZData', z( g == gu(k) ));
end
view(3)