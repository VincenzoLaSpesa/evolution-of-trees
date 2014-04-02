load fisheriris;
PL = meas(:,3);
PW = meas(:,4);
h1 = gscatter(PL,PW,species,'krb','ov^',[],'off');
set(h1,'LineWidth',2)
legend('Setosa','Versicolor','Virginica','Location','best')
hold on
X = [PL,PW];
cls = fitcdiscr(X,species);
K = cls.Coeffs(2,3).Const; % First retrieve the coefficients for the linear
L = cls.Coeffs(2,3).Linear;% boundary between the second and third classes
                           % (versicolor and virginica).

% Plot the curve K + [x,y]*L  = 0.
f = @(x1,x2) K + L(1)*x1 + L(2)*x2;
h2 = ezplot(f,[.9 7.1 0 2.5]);
set(h2,'Color','r','LineWidth',2)

% Now, retrieve the coefficients for the linear boundary between the first
% and second classes (setosa and versicolor).
K = cls.Coeffs(1,2).Const;
L = cls.Coeffs(1,2).Linear;

% Plot the curve K + [x1,x2]*L  = 0:
f = @(x1,x2) K + L(1)*x1 + L(2)*x2;
h3 = ezplot(f,[.9 7.1 0 2.5]);
set(h3,'Color','k','LineWidth',2)
axis([.9 7.1 0 2.5])
xlabel('Petal Length')
ylabel('Petal Width')
title('{\bf Linear Classification with Fisher Training Data}')