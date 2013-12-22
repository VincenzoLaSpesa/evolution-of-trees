%matrice di confusione

% genera la matrice di confusione a partire dalla classificazione reale e
% da quella ottenuta da un classificatore

function [ r ,M ] = confusion_f( reali,ottenute )
 N=length(reali);
 K=length(unique(reali));
 M=zeros(K,K);
 for i=1:N
  M(reali(i),ottenute(i))=M(reali(i),ottenute(i))+1;
 end
     
 S=sum(M');
 
 for k=1:K
  M(k,:)=M(k,:)./S(k);
 end
 %disp(M);
 r=sum(diag(M))/K;
end

